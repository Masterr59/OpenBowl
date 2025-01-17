/*
 * Copyright (C) 2020 Open Bowl <http://www.openbowlscoring.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openbowl.scorer.remote;

import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.CommonHandler;
import org.openbowl.scorer.BowlingSession;
import org.openbowl.scorer.FakeSession;
import org.openbowl.scorer.Lane;
import org.openbowl.scorer.NumberedSession;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class GameHandler extends CommonHandler {

    private BowlingSession currentSession;
    private LinkedBlockingQueue<BowlingSession> queue;
    private Lane lane;

    public GameHandler(LinkedBlockingQueue<BowlingSession> q, Lane l) {
        super();
        this.queue = q;
        this.lane = l;
        this.currentSession = null;
    }

    public void setCurrentSession(BowlingSession currentSession) {
        this.currentSession = currentSession;
    }

    public void clearCurrentSession() {
        this.currentSession = null;
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        BowlingSession gameSession = null;
        String UUID = "";
        if (parms.containsKey("UUID")) {
            UUID = parms.get("UUID");
            if (currentSession != null) {
                gameSession = currentSession.getUUID().equals(UUID) ? currentSession : null;
            }
            BowlingSession fake = new FakeSession(UUID);
            if (gameSession == null) {
                for (BowlingSession b : queue) {
                    if (b.compareTo(fake) == 0) {
                        gameSession = b;
                    }
                }
            }

        }
        if (gameSession != null) {
            map.put("UUID", UUID);
            switch (parms.getOrDefault("get", "none")) {
                case "allPlayers":
                    map.put(SUCCESS, true);
                    map.put("players", gameSession.getPlayers());
                    break;
                case "playerCount":
                    map.put(SUCCESS, true);
                    map.put("count", gameSession.getPlayers().size());
                    break;
                case "player":
                    if (parms.containsKey("id")
                            && Integer.parseInt(parms.get("id")) < gameSession.getPlayers().size()) {
                        map.put(SUCCESS, true);
                        map.put("player", gameSession.getPlayers()
                                .get(Integer.parseInt(parms.get("id"))));
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, "invalid or missing player id");
                    }
                    break;
                default:
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "missing or unsupported request");
                    break;
            }
        } else {
            switch (parms.getOrDefault("get", "none")) {
                case "allSessions":
                    ArrayList<String> allSessions = new ArrayList<>();
                    if (currentSession != null) {
                        allSessions.add(currentSession.getUUID());
                    }
                    for (BowlingSession b : queue) {
                        allSessions.add(b.getUUID());
                    }
                    map.put(SUCCESS, true);
                    map.put("allSessions", allSessions);
                    break;
                case "currentSession":
                    map.put(SUCCESS, true);
                    map.put("UUID", onGetUUID());
                    break;
                default:
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                    break;
            }

        }
        return map;
    }

    @Override
    protected Map<String, Object> onPost(Map<String, String> parms, String body) {
        Map<String, Object> map = new HashMap<>();
        BowlingSession gameSession = null;
        Map<String, Object> requestBody = new HashMap<>();
        try {
            requestBody = gson.fromJson(body, Map.class);
        } catch (JsonSyntaxException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.getMessage());
            return map;
        }
        String UUID = "";
        if (requestBody.containsKey("UUID")) {
            UUID = (String) requestBody.get("UUID");
            if (currentSession != null) {
                gameSession = currentSession.getUUID().equals(UUID) ? currentSession : null;
            }
            BowlingSession fake = new FakeSession(UUID);
            if (gameSession == null) {
                for (BowlingSession b : queue) {
                    if (b.compareTo(fake) == 0) {
                        gameSession = b;
                    }
                }
            }

        }

        map.put("UUID", UUID);
        switch (parms.getOrDefault("set", "none")) {
            case "newSession":
                if (requestBody.containsKey("type")) {
                    map.put(SUCCESS, onNewSession(requestBody));
                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session type not found");
                }
                break;
            case "newPlayer":
                if (gameSession != null) {
                    map.put(SUCCESS, onNewPlayer(requestBody, gameSession));

                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                }
                break;
            case "setPlayer":
                if (gameSession != null) {
                    map.put(SUCCESS, onSetPlayer(requestBody, gameSession));
                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                }
                break;
            case "pauseSession":
                if (gameSession != null) {
                    System.out.println("Pausing Session");
                    gameSession.pauseSession();
                    map.put(SUCCESS, true);
                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                }
                break;
            case "resumeSession":
                if (gameSession != null) {
                    gameSession.resumeSession();
                    System.out.println("Resuming Session");
                    map.put(SUCCESS, true);
                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                }
                break;
            case "abortSession":
                if (gameSession != null) {

                    gameSession.abortSession();
                    System.out.println("Aborting Session");
                    map.put(SUCCESS, true);
                } else {
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "session not found");
                }
                break;
            case "resetDisplay":
                map.put(SUCCESS, true);
                if (currentSession != null) {
                    gameSession.resetDisplay();
                }
                break;
            case "refreshDisplay":
                map.put(SUCCESS, true);
                if (currentSession != null) {
                    gameSession.refreshDisplay();
                }
                break;

            default:
                map.put(SUCCESS, false);
                map.put(ERROR_MSG, "missing or unsupported request");
                break;
        }

        return map;
    }

    private boolean onNewSession(Map<String, Object> requestBody) {
        String type = (String) requestBody.get("type");
        switch (type) {
            case "numbered":
                if (requestBody.containsKey("games") && requestBody.containsKey("UUID")) {
                    int games = new Double((double) requestBody.get("games")).intValue();
                    String UUID = (String) requestBody.get("UUID");
                    NumberedSession n = new NumberedSession(lane, games);
                    n.setUUID(UUID);
                    queue.add(n);
                    return true;
                }
                break;

        }
        return false;
    }

    private boolean onNewPlayer(Map<String, Object> requestBody, BowlingSession session) {
        for (String k : requestBody.keySet()) {
            System.out.println(k + " =>" + requestBody.get(k));
        }

        if (requestBody.containsKey("playerName") && requestBody.containsKey("playerUUID")) {
            String name = (String) requestBody.get("playerName");
            int handicap = new Double((double) requestBody.get("playerHDCP")).intValue();
            String playerUUID = (String) requestBody.get("playerUUID");
            int i = session.addPlayer(new BowlingGame(name, handicap, playerUUID, 10));
            return i >= 0;
        }

        return false;
    }

    private boolean onSetPlayer(Map<String, Object> requestBody, BowlingSession gameSession) {
        if (requestBody.containsKey("playerID") && requestBody.containsKey("player")) {
            int id = (int) requestBody.get("playerID");
            String jsonGame = gson.toJson(requestBody.get("player"));
            BowlingGame b = gson.fromJson(jsonGame, BowlingGame.class);
            gameSession.getPlayers().get(id).updateTo(b);
            return true;
        }

        return false;
    }

    private String onGetUUID() {
        if (currentSession != null) {
            return currentSession.getUUID();
        }
        return "Not in Session";
    }
}
