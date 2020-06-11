<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="modifierDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Modifier Description</div>
        <div><input type="text" class="inputBox" id="desc"></div>
        <div class="officePanelGridLabel">Modifier Category</div>
        <div><input type="text" class="inputBox" id="category"></div>
        <div class="officePanelGridLabel">Modifier Default Price</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="price"></div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var modID = 0;
    const table = "modifier";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#desc").val();
            const category = $("#category").val();
            const price = $("#price").val();

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, modifier_name:desc, modifier_category:category, modifier_price:price},
                success:function(data){
                    reloadDropdown();
                    modID = 0;
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
        if (modID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : modID, 'table' : table, 'key' : "modifier_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("modifierDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    modID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (modID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#desc").val();
                const category = $("#category").val();
                const price = $("#price").val();
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : modID, 'table' : table, 'key' : "modifier_id", modifier_name:desc, modifier_category:category, modifier_price:price},
                    success:function(data){
                        var x = document.getElementById("mod"+modID);
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
        if (modID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#desc").val("");
        $("#category").val("");
        $("#price").val("");
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM modifier';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : modID, 'table' : table, 'key' : "modifier_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#modifierDropDown").html("<option value='' id='default' onclick=''>-Select Modifier-</option>");
                var x = document.getElementById("modifierDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "mod"+JSONObject[0]["modifier_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["modifier_name"];
                    option.id = "mod" + JSONObject[i]["modifier_id"];
                    x.add(option);
                    $("#modifierDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            modID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            modID = 0;
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
        const sql = 'SELECT * FROM modifier WHERE modifier_id = ' + modID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : modID, 'table' : table, 'key' : "modifier_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#desc").val(JSONObject[0]["modifier_name"]);
                $("#category").val(JSONObject[0]["modifier_category"]);
                $("#price").val(JSONObject[0]["modifier_price"]);
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