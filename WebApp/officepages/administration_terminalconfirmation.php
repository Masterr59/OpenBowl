<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="terminalDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">*Terminal Description</div>
        <div><input type="text" class="inputBox" id="terminal_description"></div>
        <div class="officePanelGridLabel">*Computer Name</div>
        <div><input type="text" class="inputBox" id="computer_name"></div>
        <div class="officePanelGridLabel">*IP Address</div>
        <div><input type="text" class="inputBox" id="ip_address"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel">Security Device</div>
        <div>
            <input type="radio" id="security_none" name="securitydevice" value="none" checked>
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

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var tID = 0;
    const table = "terminal";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            var desc = $("#terminal_description").val();
            var name = $("#computer_name").val();
            var ip = $("#ip_address").val();
            var security = document.getElementById("security_none").checked;
            var manual = document.getElementById("manual").checked;
            var automatic = document.getElementById("automatic").checked;
            var byterminal = document.getElementById("byterminal").checked;
            var byuser = document.getElementById("byuser").checked;
            var pos = 0;
            var end_shift = 0;
            var desk_inactivity = document.getElementById("deskinactivity").checked;
            
            if (byterminal)
                end_shift = 0;
            else if (byuser)
                end_shift = 1;

            if (manual)
                pos = 0;
            else if (automatic)
                pos = 1;

            if (desk_inactivity)
                desk_inactivity = 1;
            else
                desk_inactivity = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, terminal_description:desc, computer_name:name,
                        ip_address:ip, security_device:security, pos_session_logoff:pos, end_shift_rules:end_shift, apply_desk_timeout:desk_inactivity},
                success:function(data){
                    reloadDropdown();
                    tID = 0;
                    clearForm();
                    displayMsg("A new record was successfully added into " + table, 2);
                },
                error:function(data){
                    displayMsg("An error occurred when adding a record into " + table, 1);
                }
            }
            $.ajax(ajaxRequest);
        }
        else
        {
            displayMsg("Error: Required fields cannot be blank.",1);
        }
    });

    $("#delete").on('click', function(){
        if (tID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : tID, 'table' : table, 'key' : "terminal_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("terminalDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    tID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (tID != 0)
        {
            if (checkValidData())
            {
                var desc = $("#terminal_description").val();
                var name = $("#computer_name").val();
                var ip = $("#ip_address").val();
                var security = document.getElementById("security_none").checked;
                var manual = document.getElementById("manual").checked;
                var automatic = document.getElementById("automatic").checked;
                var byterminal = document.getElementById("byterminal").checked;
                var byuser = document.getElementById("byuser").checked;
                var pos = 0;
                var end_shift = 0;
                var desk_inactivity = document.getElementById("deskinactivity").checked;
                
                if (byterminal)
                    end_shift = 0;
                else if (byuser)
                    end_shift = 1;

                if (manual)
                    pos = 0;
                else if (automatic)
                    pos = 1;

                if (desk_inactivity)
                    desk_inactivity = 1;
                else
                    desk_inactivity = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : tID, 'table' : table, 'key' : "terminal_id", terminal_description:desc, computer_name:name,
                            ip_address:ip, security_device:security, pos_session_logoff:pos, end_shift_rules:end_shift, apply_desk_timeout:desk_inactivity},
                    success:function(data){
                        var x = document.getElementById("terminal"+tID);
                        x.text = desc;
                        displayMsg("Successfully updated the details for " + desc + ".", 2);
                    },
                    error:function(data){
                        displayMsg("An error occurred when updating the details for " + desc + ".", 1);
                    }
                }
                $.ajax(ajaxRequest);
            }
            else
            {
                displayMsg("Error: Required fields cannot be blank.", 1);
            }
        }
    });

    $("#undo").on('click', function(){
        if (tID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#terminal_description").val("");
        $("#computer_name").val("");
        $("#ip_address").val("");

        $("#security_none").prop("checked", true);
        $("#manual").prop("checked", true);
        $("#automatic").prop("checked", false);
        $("#byterminal").prop("checked", true);
        $("#byuser").prop("checked", false);
        $("#deskinactivity").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM terminal';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : tID, 'table' : table, 'key' : "terminal_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#terminalDropDown").html("<option value='' id='default' onclick=''>-Select Terminal-</option>");
                var x = document.getElementById("terminalDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "terminal"+JSONObject[0]["terminal_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["terminal_description"];
                    option.id = "terminal" + JSONObject[i]["terminal_id"];
                    x.add(option);
                    $("#terminalDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            tID = parseInt(matches[0]);
                            loadFields();
                        }
                        else
                        {
                            tID = 0;
                            clearForm();
                        }

                    });
                }
            },
            error:function(JSONObject){
                displayMsg("Error: Reload Dropdown failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function loadFields()
    {
        const sql = 'SELECT * FROM terminal WHERE terminal_id = ' + tID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : tID, 'table' : table, 'key' : "terminal_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#terminal_description").val(JSONObject[0]["terminal_description"]);
                $("#computer_name").val(JSONObject[0]["computer_name"]);
                $("#ip_address").val(JSONObject[0]["ip_address"]);

                if (JSONObject[0]["security_device"]==0)
                    $("#security_none").prop("checked", true);
                else
                    $("#security_none").prop("checked", false);

                if (JSONObject[0]["pos_session_logoff"]==0)
                    $("#manual").prop("checked", true);
                else
                    $("#automatic").prop("checked", true);

                if (JSONObject[0]["end_shift_rules"]==0)
                    $("#byterminal").prop("checked", true);
                else
                    $("#byuser").prop("checked", true);

                if (JSONObject[0]["apply_desk_timeout"]==0)
                    $("#deskinactivity").prop("checked", false);
                else
                    $("#deskinactivity").prop("checked", true);
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function checkValidData()
    {
        const td = $("#terminal_description").val();
        const cn = $("#computer_name").val();
        const ip = $("#ip_address").val();

        if (td == "" || cn == "" || ip == "")
        {
            return 0;
        }
        else
        {
            return 1;
        }
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