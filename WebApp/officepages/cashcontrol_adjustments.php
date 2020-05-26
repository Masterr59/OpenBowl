<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="adjustmentDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Adjustment Description</div>
        <div><input type="text" class="inputBox" id="desc"></div>
        
        <div class="officePanelGridLabel">Item Adustment</div>
        <div>
            <input type="checkbox" id="ia" name="yesitemadjustment" value="yesitemadjustment">
            <label for="yesitemadjustment">Yes (If not checked, this item will be a transaction adjustment)</label>
        </div>

        <div class="officePanelGridLabel">Adjustment Percentage</div>
        <div><input type="text" class="inputBox" value="0.000" style="width: 100px" id="ap">10 = 10.000%</div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var adjID = 0;
    const table = "adjustment";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#desc").val();
            const ap = $("#ap").val();
            var ia = document.getElementById("ia").checked;
            if (ia)
                ia = 1;
            else
                ia = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, adjustment_name:desc, adjustment_percentage:ap, item_adjustment:ia},
                success:function(data){
                    reloadDropdown();
                    adjID = 0;
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
        if (adjID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : adjID, 'table' : table, 'key' : "adjustment_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("adjustmentDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    adjID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (adjID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#desc").val();
                const ap = $("#ap").val();
                var ia = document.getElementById("ia").checked;
                if (ia)
                    ia = 1;
                else
                    ia = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : adjID, 'table' : table, 'key' : "adjustment_id", adjustment_name:desc, adjustment_percentage:ap, item_adjustment:ia},
                    success:function(data){
                        var x = document.getElementById("adj"+adjID);
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
        if (adjID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#desc").val("");
        $("#ap").val("");
        $("#ia").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM adjustment';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : adjID, 'table' : table, 'key' : "adjustment_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#adjustmentDropDown").html("<option value='' id='default' onclick=''>-Select Adjustment-</option>");
                var x = document.getElementById("adjustmentDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "adj"+JSONObject[0]["adjustment_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["adjustment_name"];
                    option.id = "adj" + JSONObject[i]["adjustment_id"];
                    x.add(option);
                    $("#adjustmentDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            adjID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            adjID = 0;
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
        const sql = 'SELECT * FROM adjustment WHERE adjustment_id = ' + adjID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : adjID, 'table' : table, 'key' : "adjustment_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#desc").val(JSONObject[0]["adjustment_name"]);
                $("#ap").val(JSONObject[0]["adjustment_percentage"]);
                if (JSONObject[0]["item_adjustment"]==1)
                    $("#ia").prop("checked", true);
                else
                    $("#ia").prop("checked", false);
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }
    
    function checkValidData()
    {
        const data = $("#desc").val();
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