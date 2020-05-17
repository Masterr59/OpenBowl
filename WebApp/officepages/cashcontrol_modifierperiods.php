<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="mpDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Description</div>
        <div><input type="text" class="inputBox" id="mp_desc"></div>
        <div></div>
        <div>
            <input type="checkbox" id="monday" name="day" value="monday">
            <label for="monday">Monday</label><br/>
            <input type="checkbox" id="tuesday" name="day" value="tuesday">
            <label for="tuesday">Tuesday</label><br/>
            <input type="checkbox" id="wednesday" name="day" value="wednesday">
            <label for="wednesday">Wednesday</label><br/>
            <input type="checkbox" id="thursday" name="day" value="thursday">
            <label for="thursday">Thursday</label><br/>
            <input type="checkbox" id="friday" name="day" value="friday">
            <label for="friday">Friday</label><br/>
            <input type="checkbox" id="saturday" name="day" value="saturday">
            <label for="saturday">Saturday</label><br/>
            <input type="checkbox" id="sunday" name="day" value="sunday">
            <label for="sunday">Sunday</label>
        </div>
        <div class="officePanelGridLabel">Starting Time (hh:mm)</div>
        <div><input type="text" class="inputBox" value="00:00" id="starting_time"></div>
        <div class="officePanelGridLabel">Ending Time (hh:mm)</div>
        <div><input type="text" class="inputBox" value="00:00" id="ending_time"></div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var mpID = 0;
    const table = "modifier_period";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#mp_desc").val();
            const starting_time = $("#starting_time").val();
            const ending_time = $("#ending_time").val();
            var monday = document.getElementById("monday").checked;
            var tuesday = document.getElementById("tuesday").checked;
            var wednesday = document.getElementById("wednesday").checked;
            var thursday = document.getElementById("thursday").checked;
            var friday = document.getElementById("friday").checked;
            var saturday = document.getElementById("saturday").checked;
            var sunday = document.getElementById("sunday").checked;
            if (monday)
                monday = 1;
            else
                monday = 0;

            if (tuesday)
                tuesday = 1;
            else
            tuesday = 0;

            if (wednesday)
                wednesday = 1;
            else
                wednesday = 0;

            if (thursday)
                thursday = 1;
            else
                thursday = 0;

            if (friday)
                friday = 1;
            else
                friday = 0;

            if (saturday)
                saturday = 1;
            else
                saturday = 0;

            if (sunday)
                sunday = 1;
            else
                sunday = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, modifier_period_name:desc, monday:monday, tuesday:tuesday, wednesday:wednesday,
                        thursday:thursday, friday:friday, saturday:saturday, sunday:sunday, starting_time:starting_time,
                        ending_time:ending_time},
                success:function(data){
                    reloadDropdown();
                    mpID = 0;
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
        if (mpID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : dptID, 'table' : table, 'key' : "modifier_period_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("mpDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    mpID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (dptID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#mp_desc").val();
                const starting_time = $("#starting_time").val();
                const ending_time = $("#ending_time").val();
                var monday = document.getElementById("monday").checked;
                var tuesday = document.getElementById("tuesday").checked;
                var wednesday = document.getElementById("wednesday").checked;
                var thursday = document.getElementById("thursday").checked;
                var friday = document.getElementById("friday").checked;
                var saturday = document.getElementById("saturday").checked;
                var sunday = document.getElementById("sunday").checked;
                if (monday)
                    monday = 1;
                else
                    monday = 0;

                if (tuesday)
                    tuesday = 1;
                else
                tuesday = 0;

                if (wednesday)
                    wednesday = 1;
                else
                    wednesday = 0;

                if (thursday)
                    thursday = 1;
                else
                    thursday = 0;

                if (friday)
                    friday = 1;
                else
                    friday = 0;

                if (saturday)
                    saturday = 1;
                else
                    saturday = 0;

                if (sunday)
                    sunday = 1;
                else
                    sunday = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : mpID, 'table' : table, 'key' : "modifier_period_id", modifier_period_name:desc, monday:monday, tuesday:tuesday, wednesday:wednesday,
                        thursday:thursday, friday:friday, saturday:saturday, sunday:sunday, starting_time:starting_time,
                        ending_time:ending_time},
                    success:function(data){
                        var x = document.getElementById("mp"+mpID);
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
                displayMsg("Error: Required fields cannot be blank.",1);
            }
        }
    });

    $("#undo").on('click', function(){
        if (mpID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#mp_desc").val("");
        $("#starting_time").val("00:00");
        $("#ending_time").val("00:00");
        $("#monday").prop("checked", false);
        $("#tuesday").prop("checked", false);
        $("#wednesday").prop("checked", false);
        $("#thursday").prop("checked", false);
        $("#friday").prop("checked", false);
        $("#saturday").prop("checked", false);
        $("#sunday").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM modifier_period';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : mpID, 'table' : table, 'key' : "modifier_period_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#mpDropDown").html("<option value='' id='default' onclick=''>-Modifier Period-</option>");
                var x = document.getElementById("mpDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "mp"+JSONObject[0]["modifier_period_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["modifier_period_name"];
                    option.id = "mp" + JSONObject[i]["modifier_period_id"];
                    x.add(option);
                    $("#mpDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            mpID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            mpID = 0;
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
        const sql = 'SELECT * FROM modifier_period WHERE modifier_period_id = ' + mpID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : mpID, 'table' : table, 'key' : "modifier_period_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#mp_desc").val(JSONObject[0]["modifier_period_name"]);
                $("#starting_time").val(JSONObject[0]["starting_time"]);
                $("#ending_time").val(JSONObject[0]["ending_time"]);
                if (JSONObject[0]["monday"]==1)
                    $("#monday").prop("checked", true);
                else
                    $("#monday").prop("checked", false);

                    if (JSONObject[0]["tuesday"]==1)
                    $("#tuesday").prop("checked", true);
                else
                    $("#tuesday").prop("checked", false);

                    if (JSONObject[0]["wednesday"]==1)
                    $("#wednesday").prop("checked", true);
                else
                    $("#wednesday").prop("checked", false);

                    if (JSONObject[0]["thursday"]==1)
                    $("#thursday").prop("checked", true);
                else
                    $("#thursday").prop("checked", false);

                    if (JSONObject[0]["friday"]==1)
                    $("#friday").prop("checked", true);
                else
                    $("#friday").prop("checked", false);

                    if (JSONObject[0]["saturday"]==1)
                    $("#saturday").prop("checked", true);
                else
                    $("#saturday").prop("checked", false);

                    if (JSONObject[0]["sunday"]==1)
                    $("#sunday").prop("checked", true);
                else
                    $("#sunday").prop("checked", false);
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function checkValidData()
    {
        const data = $("#mp_desc").val();
        if (data == "")
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