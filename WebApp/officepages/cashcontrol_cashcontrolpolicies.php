<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">

        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>

        <div class="officePanelColumnLabel" style="text-align: right">Daily Sales</div>
        <div></div>

        <div class="officePanelGridLabel">Fiscal Day Start Time (hh:mm)</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="dst"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Tabs</div>
        <div></div>

        <div class="officePanelGridLabel">Maximum Tab Amount</div>
        <div><input type="text" class="inputBox" style="width: 100px" id="mta"></div>
        <div class="officePanelGridLabel">Tab Refresh Time (minutes)</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="trt"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="act" name="automaticallyclosetabs" value="automaticallyclosetabs">
        <label for="automaticallyclosetabs">Automatically close all open tabs at the end of the fiscal day</label></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Transaction History</div>
        <div></div>

        <div class="officePanelGridLabel">Number of days to save the Transaction History</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="dth"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Lane Auto-Shutdown</div>
        <div></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="asm" name="allowautoshutdown" value="allowautoshutdown">
        <label for="allowautoshutdown">Allow use of Lane Auto-Shutdown mode (post-paid game/time limits)</label></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Euro Currency</div>
        <div></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="sec" name="showeurocurrency" value="showeurocurrency">
        <label for="showeurocurrency">Show Euro Currency Totals</label></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Euro Currency Conversion Value</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="ecc"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">End Shift</div>
        <div></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="acs" name="autocloseshifts" value="autocloseshifts" checked>
        <label for="autocloseshifts">Automatically close all shifts at the end of the fiscal day</label></div>
    </div>

</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    const table = "cash_tool_policies";
    loadFields();
    $("#edit").on('click', function(){
        var act = document.getElementById("act").checked;
        var asm = document.getElementById("asm").checked;
        var sec = document.getElementById("sec").checked;
        var acs = document.getElementById("acs").checked;

        var a = $("#dst").val();
        var b = $("#mta").val();
        var c = $("#trt").val();
        var d = $("#dth").val();
        var e = $("#ecc").val();

        var timeout = parseInt($("#timeout").val());

        if (act)
            act = 1;
        else
            act = 0;

        if (asm)
            asm = 1;
        else
            asm = 0;

        if (sec)
            sec = 1;
        else
            sec = 0;

        if (acs)
            acs = 1;
        else
            acs = 0;
        
        var ajaxRequest = {
            url: './update.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "policy_id", day_start_time:a, maximum_tab_amount:b,
                    tab_refresh_time:c, close_tabs_end_of_day:act, days_to_save_history:d,
                    allow_lane_auto_shutdown:asm, show_euro_currency_tools:sec, euro_currency_conversion_value:e,
                    close_shifts_end_of_day:acs},
            success:function(data){
                displayMsg("Successfully updated the details for Policies", 2);
            },
            error:function(data){
                displayMsg("An error occurred when updating the details for Policies", 1);
            }
        }
        $.ajax(ajaxRequest);
    });

    $("#undo").on('click', function(){
        loadFields();
    });

    function loadFields()
    {
        const sql = 'SELECT * FROM cash_control_policies WHERE policy_id = 1';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "policy_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#dst").val(JSONObject[0]["day_start_time"]);
                $("#mta").val(JSONObject[0]["maximum_tab_amount"]);
                $("#trt").val(JSONObject[0]["tab_refresh_time"]);
                $("#dth").val(JSONObject[0]["days_to_save_history"]);
                $("#ecc").val(JSONObject[0]["euro_currency_conversion_value"]);

                if (JSONObject[0]["close_tabs_end_of_day"]==1)
                    $("#act").prop("checked", true);
                else
                    $("#act").prop("checked", false);

                if (JSONObject[0]["allow_lane_auto_shutdown"]==1)
                    $("#asm").prop("checked", true);
                else
                    $("#asm").prop("checked", false);

                if (JSONObject[0]["show_euro_currency_tools"]==1)
                    $("#sec").prop("checked", true);
                else
                    $("#sec").prop("checked", false);

                if (JSONObject[0]["close_shifts_end_of_day"]==1)
                    $("#acs").prop("checked", true);
                else
                    $("#acs").prop("checked", false);
                    
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function displayMsg(msg, type) {
        switch (type)
        {
        case 1:
            $("#errorMsgContainer").html("");
            $("#errorMsgContainer").append("<div class=\"errorMsg\"><i class=\"fa fa-exclamation-circle\"></i>&nbsp;" + msg + "</div>");
            break;
        case 2:
            $("#errorMsgContainer").html("");
            $("#errorMsgContainer").append("<div class=\"successMsg\"><i class=\"fa fa-check-circle\"></i>&nbsp;" + msg + "</div>");
            break;
        }
    }
});
</script>