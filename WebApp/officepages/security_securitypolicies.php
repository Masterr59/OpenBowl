<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">
        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>
        <div class="officePanelGridLabel">Security devices installed at center</div>
        <div>
            <input type="checkbox" id="cardreaders" name="securitydevice" value="cardreaders">
            <label for="cardreaders">Card readers</label>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Password rules</div>
        <div>
            <input type="checkbox" id="resetpasswordfirstlogin" name="passwordrules" value="resetpasswordfirstlogin">
            <label for="resetpasswordfirstlogin">Reset password at first login</label><br/>
            <input type="checkbox" id="enforcepasswordhistory" name="passwordrules" value="enforcepasswordhistory">
            <label for="enforcepasswordhistory">Enforce password history</label><br/>
            <input type="checkbox" id="enforcestrongerpasswords" name="passwordrules" value="enforcestrongerpasswords">
            <label for="enforcestrongerpasswords">Enforce strong passwords</label>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Maximum days a user password stays active</div>
        <div>
            <select id="dayspassactive" class="dropdownBox">
                <option value="always" id="a1">Always</option>
                <option value="30" id="a2">30</option>
                <option value="60" id="a3">60</option>
                <option value="90" id="a4">90</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Maximum days of inactivity a user account stays active</div>
        <div>
            <select id="daysinactivity" class="dropdownBox">
                <option value="always" id="b1">Always</option>
                <option value="30" id="b2">30</option>
                <option value="60" id="b3">60</option>
                <option value="90" id="b4">90</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Maximum failed access attempts before user is locked out</div>
        <div>
            <select id="attempslockout" class="dropdownBox">
                <option value="unlimited" id="c1">Unlimited</option>
                <option value="1" id="c2">1</option>
                <option value="2" id="c3">2</option>
                <option value="3" id="c4">3</option>
                <option value="4" id="c5">4</option>
                <option value="5" id="c6">5</option>
            </select>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Inactive OpenBowl timeout (Minutes)</div>
        <div><input type="text" class="inputBox" style="width: 40px" id="timeout"></div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    const table = "policies";
    loadFields();
    $("#edit").on('click', function(){
        var card_readers = document.getElementById("cardreaders").checked;
        var reset_pass_first_login = document.getElementById("resetpasswordfirstlogin").checked;
        var enforce_pass_history = document.getElementById("enforcepasswordhistory").checked;
        var enforce_strong_passwords = document.getElementById("enforcestrongerpasswords").checked;

        var a = $("#dayspassactive").val();
        var b = $("#daysinactivity").val();
        var c = $("#attempslockout").val();

        if (a == "always")
            a = 0;
        if (b == "always")
            b = 0;
        if (c == "unlimited")
            c = 0;

        var timeout = parseInt($("#timeout").val());

        if (card_readers)
            card_readers = 1;
        else
            card_readers = 0;

        if (reset_pass_first_login)
            reset_pass_first_login = 1;
        else
            reset_pass_first_login = 0;

        if (enforce_pass_history)
            enforce_pass_history = 1;
        else
            enforce_pass_history = 0;

        if (enforce_strong_passwords)
            enforce_strong_passwords = 1;
        else
            enforce_strong_passwords = 0;
        
        var ajaxRequest = {
            url: './update.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "policy_id", card_readers:card_readers, reset_password_first_login:reset_pass_first_login,
                    enforce_password_history:enforce_pass_history, enforce_strong_passwords:enforce_strong_passwords, max_days_pass_active:a,
                    max_days_inactivity:b, max_failed_attempts:c, inactive_timeout:timeout},
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
        const sql = 'SELECT * FROM policies WHERE policy_id = 1';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "policy_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#timeout").val(JSONObject[0]["inactive_timeout"]);

                switch(parseInt(JSONObject[0]["max_failed_attempts"]))
                {
                    case(0):
                        $('#attempslockout option:eq(0)').prop('selected', true);
                        break;
                    case(1):
                        $('#attempslockout option:eq(1)').prop('selected', true);
                        break;
                    case(2):
                        $('#attempslockout option:eq(2)').prop('selected', true);
                        break;
                    case(3):
                        $('#attempslockout option:eq(3)').prop('selected', true);
                        break;
                    case(4):
                        $('#attempslockout option:eq(4)').prop('selected', true);
                        break;
                    case(5):
                        $('#attempslockout option:eq(5)').prop('selected', true);
                        break;
                }

                switch(parseInt(JSONObject[0]["max_days_inactivity"]))
                {
                    case(0):
                        $('#daysinactivity option:eq(0)').prop('selected', true);
                        break;
                    case(30):
                        $('#daysinactivity option:eq(1)').prop('selected', true);
                        break;
                    case(60):
                        $('#daysinactivity option:eq(2)').prop('selected', true);
                        break;
                    case(90):
                        $('#daysinactivity option:eq(3)').prop('selected', true);
                        break;
                }

                switch(parseInt(JSONObject[0]["max_days_pass_active"]))
                {
                    case(0):
                        $('#dayspassactive option:eq(0)').prop('selected', true);
                        break;
                    case(30):
                        $('#dayspassactive option:eq(1)').prop('selected', true);
                        break;
                    case(60):
                        $('#dayspassactive option:eq(2)').prop('selected', true);
                        break;
                    case(90):
                        $('#dayspassactive option:eq(3)').prop('selected', true);
                        break;
                }

                if (JSONObject[0]["enforce_strong_passwords"]==1)
                    $("#enforcestrongerpasswords").prop("checked", true);
                else
                    $("#enforcestrongerpasswords").prop("checked", false);

                if (JSONObject[0]["enforce_password_history"]==1)
                    $("#enforcepasswordhistory").prop("checked", true);
                else
                    $("#enforcepasswordhistory").prop("checked", false);

                if (JSONObject[0]["reset_password_first_login"]==1)
                    $("#resetpasswordfirstlogin").prop("checked", true);
                else
                    $("#resetpasswordfirstlogin").prop("checked", false);

                if (JSONObject[0]["card_readers"]==1)
                    $("#cardreaders").prop("checked", true);
                else
                    $("#cardreaders").prop("checked", false);
                    
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