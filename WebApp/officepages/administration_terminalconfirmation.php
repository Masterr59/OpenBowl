<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="servers" class="dropdownBox">
                <option value="test1">test server 1</option>
                <option value="test2">test server 2</option>
                <option value="test3">test server 3</option>
            </select>
        </div>
        <div>
            <button type="submit" class="saveBtn"><i class="fa fa-file"></i></button>
            <button type="submit" class="saveBtn"><i class="fa fa-save"></i></button>
            <button type="submit" class="saveBtn"><i class="fa fa-undo"></i></button>
            <button type="submit" class="saveBtn"><i class="fa fa-trash"></i></button>
        </div>
        <div class="officePanelGridLabel">Terminal Description</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Computer Name</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">IP Address</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Terminal ID</div>
        <div><input type="text" class="inputBox"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel">Security Device</div>
        <div>
            <input type="radio" id="none" name="securitydevice" value="none" checked>
            <label for="none">None</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel">POS Session Logoff</div>
        <div>
            <input type="radio" id="manual" name="pos" value="manual" checked>
            <label for="manual">Manually by User (Session ends when the user logs off)</label><br/>
            <input type="radio" id="automatic" name="pos" value="automatic">
            <label for="automatic">Automatic (Session ends immediately after a transaction is closed)</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel">End Shift Rules</div>
        <div>
            <input type="radio" id="byterminal" name="endshift" value="byterminal" checked>
            <label for="byterminal">By Terminal (Closes all transactions for this terminal)</label><br/>
            <input type="radio" id="byuser" name="endshift" value="byuser">
            <label for="byuser">By User (Closes transactions for the current user)</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div></div>
        <div>
            <input type="checkbox" id="deskinactivity" name="deskinactivity" value="deskinactivity">
            <label for="deskinactivity">Apply Desk Inactivity Timeout</label>
        </div>
    </div>
</div>