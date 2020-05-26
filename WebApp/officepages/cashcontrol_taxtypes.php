<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="taxDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Tax Description</div>
        <div><input type="text" class="inputBox" id="desc"></div>
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartmentDropDown" class="dropdownBox"></select>
        <div class="officePanelGridLabel">Tax Amount (7 = 7.000%)</div>
        <div><input type="text" class="inputBox" value="0" id="ta"></div>
        <div class="officePanelGridLabel">Transaction Tax</div>
        <div>
            <input type="checkbox" id="tt" name="yestransactiontax" value="yestransactiontax">
            <label for="yestransactiontax">Yes</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div style="margin-top: 10px;">
        <b>Products</b> associated with this Tax Type<br/>
        
        <div style="margin-top: 10px;">
            <table class="officePanelTable" style="margin-bottom: 10px;">
                <thead>
                    <tr>
                        <th>Name</th>
                    </tr>
                </thead>
                <tbody id="productTable">
                    
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var taxID = 0;
    var subdptID = 0;
    const table = "tax_type";
    getSubDepartments();
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#desc").val();
            const ta = $("#ta").val();
            var tt = document.getElementById("tt").checked;
            if (tt)
                    tt = 1;
                else
                    tt = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, tax_type_name:desc, tax_rate:ta, sub_depart_id:subdptID, transaction_tax:tt},
                success:function(data){
                    reloadDropdown();
                    taxID = 0;
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
        if (taxID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : taxID, 'table' : table, 'key' : "tax_type_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("taxDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    taxID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (taxID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#desc").val();
                const ta = $("#ta").val();
                var tt = document.getElementById("tt").checked;
                if (tt)
                        tt = 1;
                    else
                        tt = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : taxID, 'table' : table, 'key' : "tax_type_id", tax_type_name:desc, tax_rate:ta, sub_depart_id:subdptID, transaction_tax:tt},
                    success:function(data){
                        var x = document.getElementById("tax"+taxID);
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
        if (taxID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#desc").val("");
        $("#ta").val("");
        $("#tt").prop("checked", false);
        $("#productTable").html("");
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM tax_type';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : taxID, 'table' : table, 'key' : "tax_type_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#taxDropDown").html("<option value='' id='default' onclick=''>-Select Tax Type-</option>");
                var x = document.getElementById("taxDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "tax"+JSONObject[0]["tax_type_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["tax_type_name"];
                    option.id = "tax" + JSONObject[i]["tax_type_id"];
                    x.add(option);
                    $("#taxDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            taxID = parseInt(matches[0]);
                            loadFields();
                        }
                        else
                        {
                            taxID = 0;
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
        const sql = 'SELECT * FROM tax_type WHERE tax_type_id = ' + taxID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : taxID, 'table' : table, 'key' : "tax_type_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#desc").val(JSONObject[0]["tax_type_name"]);
                $("#ta").val(JSONObject[0]["tax_rate"]);
                if (JSONObject[0]["transaction_tax"]==1)
                    $("#tt").prop("checked", true);
                else
                    $("#tt").prop("checked", false);
                subdptID = JSONObject[0]["sub_depart_id"];
                var k = "subdpt"+JSONObject[0]["sub_depart_id"];
                $('#subdepartmentDropDown option[id="'+k+'"]').attr("selected", "selected");
                getProducts();
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
    function getProducts()
    {
        const sql = 'SELECT * FROM product WHERE tax_type_id = ' + taxID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : taxID, 'table' : "product", 'key' : "tax_type_id", 'sql' : sql},
            dataType: 'json',
            success:function(JSONObject){
                $("#productTable").html("");
                for(var i = 0; i < JSONObject.length; i++)
                {
                    $("#productTable").append("<tr><td>" + JSONObject[i]["product_name"] +"</td></tr>");
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