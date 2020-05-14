<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <form method="post" action="" accept-charset="UTF-8" id="officeform">
        <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
            
            <div class="officePanelGridLabel" style="margin-top: 0px" id="dropdownHandler">
                <select name='departments' class='dropdownBox' id='departmentDropDown'></select>
            </div>

            <div style="display: flex;">
                <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
                <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
                <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
                <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
            </div>
            <div class="officePanelGridLabel">*Department Description</div>
            <div><input type="text" class="inputBox" name="dpt_desc" id="dpt_desc"></div>
            <div class="officePanelGridLabel">Department Identifier</div>
            <div><input type="text" class="inputBox" name="dpt_ident" id="dpt_ident"></div>
            <div></div>
            <div>
                <input type="checkbox" id="excludefromsales" name="exclude" value="excludefromsales">
                <label for="excludefromsales">Exclude from Sales</label>
            </div>
            
        </div>
    </form>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div style="margin-top: 10px;">
        <b>Sub Departments</b> associated with this Department<br/>
        
        <div style="margin-top: 10px;">
            <table class="officePanelTable" style="margin-bottom: 10px;">
                <thead>
                    <tr>
                        <th>Name</th>
                    </tr>
                </thead>
                <tbody id="subdepartmentTable">
                    
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var dptID = 0;
    const table = "department";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#dpt_desc").val();
            const ident = $("#dpt_ident").val();
            var exclude = document.getElementById("excludefromsales").checked;
            if (exclude)
                    exclude = 1;
                else
                    exclude = 0;
            const createdby = 4;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, depart_name:desc, created_by:createdby, depart_identifier:ident, exclude_from_sales:exclude},
                success:function(data){
                    reloadDropdown();
                    dptID = 0;
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
        if (dptID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : dptID, 'table' : table, 'key' : "depart_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("departmentDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    dptID = 0;
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
                var desc = $("#dpt_desc").val();
                var ident = $("#dpt_ident").val();
                var exclude = document.getElementById("excludefromsales").checked;
                if (exclude)
                    exclude = 1;
                else
                    exclude = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : dptID, 'table' : table, 'key' : "depart_id", depart_name:desc, depart_identifier:ident, exclude_from_sales:exclude},
                    success:function(data){
                        var x = document.getElementById("dpt"+dptID);
                        x.text = desc;
                        displayMsg("Successfully updated the details for " + desc + ".", 2);
                    },
                    error:function(data){
                        displayMsg("An error occurred when updating the details for " + desc + ".", 1);
                    }
                }
                $.ajax(ajaxRequest);
            }
        }
    });

    $("#undo").on('click', function(){
        if (dptID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#dpt_desc").val("");
        $("#dpt_ident").val("");
        $("#excludefromsales").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM department';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : dptID, 'table' : table, 'key' : "depart_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#departmentDropDown").html("<option value='' id='default' onclick=''>-Select Department-</option>");
                var x = document.getElementById("departmentDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "dpt"+JSONObject[0]["depart_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["depart_name"];
                    option.id = "dpt" + JSONObject[i]["depart_id"];
                    x.add(option);
                    $("#departmentDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            dptID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            dptID = 0;
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
        const sql = 'SELECT * FROM department WHERE depart_id = ' + dptID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : dptID, 'table' : table, 'key' : "depart_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#dpt_desc").val(JSONObject[0]["depart_name"]);
                $("#dpt_ident").val(JSONObject[0]["depart_identifier"]);
                if (JSONObject[0]["exclude_from_sales"]==1)
                    $("#excludefromsales").prop("checked", true);
                else
                    $("#excludefromsales").prop("checked", false);
                getSubDepartments();
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function getSubDepartments()
    {
        const sql = 'SELECT * FROM sub_department WHERE depart_id = ' + dptID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : dptID, 'table' : "sub_department", 'key' : "depart_id", 'sql' : sql},
            dataType: 'json',
            success:function(JSONObject){
                $("#subdepartmentTable").html("");
                for(var i = 0; i < JSONObject.length; i++)
                {
                    $("#subdepartmentTable").append("<tr><td>" + JSONObject[i]["sub_depart_name"] +"</td></tr>");
                }
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function checkValidData()
    {
        const data = $("#dpt_desc").val();
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