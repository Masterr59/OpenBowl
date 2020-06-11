<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="bankingDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Banking Description</div>
        <div><input type="text" class="inputBox" id="desc"></div>
        
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartmentDropDown" class="dropdownBox"></select>

        <div></div>
        <div>
            <input type="checkbox" id="payout" name="payout" value="payout">
            <label for="payout">Pay Out</label>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var bankID = 0;
    var subdptID = 0;
    const table = "banking";
    getSubDepartments();
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#desc").val();
            var payout = document.getElementById("payout").checked;
            if (payout)
                payout = 1;
            else
                payout = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, banking_name:desc, sub_depart_id:subdptID, pay_out:payout},
                success:function(data){
                    reloadDropdown();
                    bankID = 0;
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
        if (bankID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : bankID, 'table' : table, 'key' : "sub_depart_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("subdepartmentDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    bankID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (bankID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#desc").val();
                var payout = document.getElementById("payout").checked;
                if (payout)
                    payout = 1;
                else
                    payout = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : bankID, 'table' : table, 'key' : "banking_id", banking_name:desc, sub_depart_id:subdptID, pay_out:payout},
                    success:function(data){
                        console.log(data);
                        var x = document.getElementById("bank"+bankID);
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
        if (bankID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#desc").val("");
        $("#pay_out").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM banking';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : bankID, 'table' : table, 'key' : "banking_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#bankingDropDown").html("<option value='' id='default' onclick=''>-Select Banking-</option>");
                var x = document.getElementById("bankingDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "bank"+JSONObject[0]["banking_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["banking_name"];
                    option.id = "bank" + JSONObject[i]["banking_id"];
                    x.add(option);
                    $("#bankingDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            bankID = parseInt(matches[0]);
                            loadFields();
                        }
                        else
                        {
                            bankID = 0;
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
        const sql = 'SELECT * FROM banking WHERE banking_id = ' + bankID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : bankID, 'table' : table, 'key' : "banking_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#desc").val(JSONObject[0]["banking_name"]);

                if (JSONObject[0]["pay_out"]==1)
                    $("#payout").prop("checked", true);
                else
                    $("#payout").prop("checked", false);

                subdptID = parseInt(JSONObject[0]["sub_depart_id"]);

                var k = "subdpt"+JSONObject[0]["sub_depart_id"];
                $('#subdepartmentDropDown option[id="'+k+'"]').attr("selected", "selected");
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }
    function getSubDepartments()
    {
        const sql = 'SELECT * FROM sub_department';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : subdptID, 'table' : table, 'key' : "sub_depart_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#subdepartmentDropDown").html("<option value='' id='defaultSubdpt' onclick=''>-Select SubDepartment-</option>");
                var x = document.getElementById("subdepartmentDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "subdpt"+JSONObject[0]["sub_depart_id"];

                    var option = document.createElement("option");
                    option.text = JSONObject[i]["sub_depart_name"];
                    option.id = "subdpt" + JSONObject[i]["sub_depart_id"];
                    x.add(option);
                    $("#subdepartmentDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#defaultSubdpt")
                        {
                            var matches = s.match(/(\d+)/);
                            subdptID = parseInt(matches[0]);
                        }
                        else
                        {
                            subdptID = 0;
                        }

                    });
                }

            },
            error:function(JSONObject){
                displayMsg("Error: Sub Department Reload Dropdown failed", 1);
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