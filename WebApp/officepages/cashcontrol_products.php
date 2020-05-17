<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="productDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div></div>
        <div>
            <input type="radio" id="standard" name="game_type" value="standard" checked>
            <label for="standard">Standard</label><br/>
            <input type="radio" id="game_bowling" name="game_type" value="game_bowling">
            <label for="game_bowling">Game Bowling</label><br/>
            <input type="radio" id="time_bowling" name="game_type" value="time_bowling">
            <label for="time_bowling">Time Bowling</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr; margin-top: 10px;">
        <div class="officePanelGridLabel">Description</div>
        <div><input type="text" class="inputBox" id="prod_desc"></div>
        <div class="officePanelGridLabel">Stock Keeping Unit (SKU)</div>
        <div><input type="text" class="inputBox" id="sku"></div>
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartmentDropDown" class="dropdownBox"></select>
        <div class="officePanelGridLabel">Cash Register Order</div>
        <div>
            <input type="text" class="inputBox" style="width: 50px" id="cash_register_order">
            <input type="checkbox" id="out_of_stock" name="outofstock" value="outofstock">
            <label for="outofstock">Out of Stock</label>
        </div>
        <div class="officePanelGridLabel">Price</div>
        <div>
            <input type="text" class="inputBox" style="width: 75px" id="price" value="$0.00">
            <input type="checkbox" id="always_open_price" name="alwaysopenprice" value="alwaysopenprice">
            <label for="alwaysopenprice">Always Open price</label>
        </div>
        <div class="officePanelGridLabel">Sold By</div>
        <div>
            <input type="radio" id="per_unit" name="soldby" value="perunit" checked>
            <label for="per_unit">Per Unit</label>
            <input type="radio" id="per_hour" name="soldby" value="perhour">
            <label for="per_hour">Per Hour</label>
        </div>
        <div class="officePanelGridLabel">Tax Type</div>
        <select id="taxtypeDropDown" class="dropdownBox"></select>

        <div class="officePanelGridLabel">Enable Modifiers</div>
        <div style="display: flex;">
            <input type="checkbox" id="enable_modifiers" name="enablemodifiers" value="enablemodifiers">
            <label for="enablemodifiers"></label><div class="saveBtn" id="add_modifier"><i class="fa fa-plus"></i></div>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var prodID = 0;
    var subdptID = 0;
    var taxtypeID = 0;
    const table = "product";
    getSubDepartments();
    getTaxTypes();
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#prod_desc").val();
            const sku = $("#sku").val();
            const cro = $("#cash_register_order").val();
            const price = $("#price").val();
            var pti = 0;
            var standard = document.getElementById("standard").checked;
            var game_bowling = document.getElementById("game_bowling").checked;
            var time_bowling = document.getElementById("time_bowling").checked;
            var oos = document.getElementById("out_of_stock").checked;
            var aop = document.getElementById("always_open_price").checked;
            var pu = document.getElementById("per_unit").checked;
            var ph = document.getElementById("per_hour").checked;
            var sold_by = 0;
            var em = document.getElementById("enable_modifiers").checked;

            if (oos)
                oos = 1;
            else
                oos = 0;

            if (aop)
                aop = 1;
            else
                aop = 0;

            if (pu)
                sold_by = 0;
            else if(ph)
                sold_by = 1;

            if (standard)
                pti = 1;
            else if (game_bowling)
                pti = 2;
            else if (time_bowling)
                pti = 3;

            if (em)
                em = 1;
            else
                em = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, product_name:desc, stock_keeping_unit:sku, cash_register_order:cro, sold_by:sold_by,
                        enable_modifiers:em, always_open_price:aop, price:price, out_of_stock:oos, product_type_id:pti,
                        sub_depart_id:subdptID, tax_type_id:taxtypeID},
                success:function(data){
                    reloadDropdown();
                    prodID = 0;
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
        if (subdptID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : prodID, 'table' : table, 'key' : "product_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("productDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    prodID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (prodID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#prod_desc").val();
                const sku = $("#sku").val();
                const cro = $("#cash_register_order").val();
                const price = $("#price").val();
                var standard = document.getElementById("standard").checked;
                var game_bowling = document.getElementById("game_bowling").checked;
                var time_bowling = document.getElementById("time_bowling").checked;
                var oos = document.getElementById("out_of_stock").checked;
                var aop = document.getElementById("always_open_price").checked;
                var pu = document.getElementById("per_unit").checked;
                var ph = document.getElementById("per_hour").checked;
                var sold_by = 0;
                var em = document.getElementById("enable_modifiers").checked;
                var pti = 0;

                if (oos)
                    oos = 1;
                else
                    oos = 0;

                if (aop)
                    aop = 1;
                else
                    aop = 0;

                if (pu)
                    sold_by = 0;
                else if(ph)
                    sold_by = 1;

                if (standard)
                    pti = 1;
                else if (game_bowling)
                    pti = 2;
                else if (time_bowling)
                    pti = 3;

                if (em)
                    em = 1;
                else
                    em = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : prodID, 'table' : table, 'key' : "product_id", product_name:desc, stock_keeping_unit:sku, cash_register_order:cro, sold_by:sold_by,
                        enable_modifiers:em, always_open_price:aop, price:price, out_of_stock:oos, product_type_id:pti, sub_depart_id:subdptID, tax_type_id:taxtypeID},
                    success:function(data){
                        var x = document.getElementById("prod"+prodID);
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
        if (prodID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#prod_desc").val("");
        $("#sku").val("");
        $("#cash_register_order").val("");
        $("#price").val("");

        $("#out_of_stock").prop("checked", false);
        $("#enable_modifiers").prop("checked", false);
        $("#per_unit").prop("checked", true);
        $("#per_hour").prop("checked", false);
        $("#always_open_price").prop("checked", false);
        $("#standard").prop("checked", true);
        $("#game_bowling").prop("checked", false);
        $("#time_bowling").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM product';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : prodID, 'table' : table, 'key' : "product_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#productDropDown").html("<option value='' id='default' onclick=''>-Select Product-</option>");
                var x = document.getElementById("productDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "prod"+JSONObject[0]["product_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["product_name"];
                    option.id = "prod" + JSONObject[i]["product_id"];
                    x.add(option);
                    $("#productDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            prodID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            prodID = 0;
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
        const sql = 'SELECT * FROM product WHERE product_id = ' + prodID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : prodID, 'table' : table, 'key' : "product_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                subdptID = JSONObject[0]["sub_depart_id"];
                taxtypeID = JSONObject[0]["tax_type_id"];
                $("#prod_desc").val(JSONObject[0]["product_name"]);
                $("#sku").val(JSONObject[0]["stock_keeping_unit"]);
                $("#cash_register_order").val(JSONObject[0]["cash_register_order"]);
                $("#price").val(JSONObject[0]["price"].toLocaleString('us-US', { style: 'currency', currency: 'USD'}));

                if (JSONObject[0]["out_of_stock"]==1)
                    $("#out_of_stock").prop("checked", true);
                else
                    $("#out_of_stock").prop("checked", false);

                if (JSONObject[0]["always_open_price"]==1)
                    $("#always_open_price").prop("checked", true);
                else
                    $("#always_open_price").prop("checked", false);

                if (JSONObject[0]["enable_modifiers"]==1)
                    $("#enable_modifiers").prop("checked", true);
                else
                    $("#enable_modifiers").prop("checked", false);

                var k = "subdpt"+subdptID;
                $('#subdepartmentDropDown option[id="'+k+'"]').attr("selected", "selected");

                var l = "tax_type"+taxtypeID;
                $('#taxtypeDropDown option[id="'+l+'"]').attr("selected", "selected");

                if (JSONObject[0]["sold_by"]==1)
                {
                    $("#per_hour").prop("checked", true);
                    $("#per_unit").prop("checked", false);
                }
                else
                {
                    $("#per_unit").prop("checked", true);
                    $("#per_hour").prop("checked", false);
                }

                switch(parseInt(JSONObject[0]["product_type_id"]))
                {
                    case 1:
                        $("#standard").prop("checked", true);
                        $("#game_bowling").prop("checked", false);
                        $("#time_bowling").prop("checked", false);
                        break;
                    case 2:
                        $("#standard").prop("checked", false);
                        $("#game_bowling").prop("checked", true);
                        $("#time_bowling").prop("checked", false);
                        break;
                    case 3:
                        $("#standard").prop("checked", false);
                        $("#time_bowling").prop("checked", true);
                        $("#game_bowling").prop("checked", false);
                        break;
                }
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
            data: { 'id' : prodID, 'table' : table, 'key' : "sub_depart_id", 'sql' : sql },
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

    function getTaxTypes()
    {
        const sql = 'SELECT * FROM tax_type';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : taxtypeID, 'table' : table, 'key' : "tax_type_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#taxtypeDropDown").html("<option value='' id='defaultTaxtype' onclick=''>-Select Tax Type-</option>");
                var x = document.getElementById("taxtypeDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "tax_type"+JSONObject[0]["tax_type_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["tax_type_name"];
                    option.id = "tax_type" + JSONObject[i]["tax_type_id"];
                    x.add(option);
                    $("#taxtypeDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#defaultTaxtype")
                        {
                            var matches = s.match(/(\d+)/);
                            taxtypeID = parseInt(matches[0]);
                        }
                        else
                        {
                            taxtypeID = 0;
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

        const data = $("#prod_desc").val();
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