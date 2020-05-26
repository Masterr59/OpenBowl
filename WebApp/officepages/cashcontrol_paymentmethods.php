<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="paymentDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Payment Method Description</div>
        <div><input type="text" class="inputBox" id="desc"></div>
        
        <div></div>
        <div>
            <input type="radio" id="cash" name="paymentmethod" value="cash" checked>
            <label for="cash">Cash</label>
            <input type="radio" id="check" name="paymentmethod" value="check">
            <label for="check">Check</label>
            <input type="radio" id="giftcertificate" name="paymentmethod" value="giftcertificate">
            <label for="giftcertificate">Gift Certificate</label>
            <input type="radio" id="other" name="paymentmethod" value="other">
            <label for="other">Other</label>
        </div>

        <div></div>
        <div>
            <input type="checkbox" id="credentialsrequired" name="credentialsrequired" value="credentialsrequired">
            <label for="credentialsrequired">Credentials required</label><br/>
            <input type="checkbox" id="allowovertender" name="allowovertender" value="allowovertender">
            <label for="allowovertender">Allow over tender</label>
            <input type="text" class="inputBox" placeholder="$0.00" style="width: 75px" id="tender"><br/>
            <input type="checkbox" id="makedefaultmethod" name="makedefaultmethod" value="makedefaultmethod">
            <label for="makedefaultmethod">Make this the default payment method</label>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var pmID = 0;
    const table = "payment_method";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const desc = $("#desc").val();
            const tender = $("#tender").val();
            var type = 0;

            if (document.getElementById("cash").checked)
                type = 1;
            else if (document.getElementById("check").checked)
                type = 2;
            else if (document.getElementById("giftcertificate").checked)
                type = 3;
            else if (document.getElementById("other").checked)
                type = 4;

            var cr = document.getElementById("credentialsrequired").checked;
            if (cr)
                    cr = 1;
                else
                    cr = 0;

            var aot = document.getElementById("allowovertender").checked;
            if (aot)
                    aot = 1;
                else
                    aot = 0;

            var dp = document.getElementById("makedefaultmethod").checked;
            if (dp)
                    dp = 1;
                else
                    dp = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, payment_method_name:desc, tender_amount:tender, payment_type:type, credentials_required:cr,
                        allow_over_tender:aot, default_method:dp},
                success:function(data){
                    reloadDropdown();
                    pmID = 0;
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
        if (pmID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : pmID, 'table' : table, 'key' : "payment_method_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("paymentDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    pmID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (pmID != 0)
        {
            if (checkValidData())
            {
                const desc = $("#desc").val();
                const tender = $("#tender").val();
                var type = 0;

                if (document.getElementById("cash").checked)
                    type = 1;
                else if (document.getElementById("check").checked)
                    type = 2;
                else if (document.getElementById("giftcertificate").checked)
                    type = 3;
                else if (document.getElementById("other").checked)
                    type = 4;

                var cr = document.getElementById("credentialsrequired").checked;
                if (cr)
                        cr = 1;
                    else
                        cr = 0;

                var aot = document.getElementById("allowovertender").checked;
                if (aot)
                        aot = 1;
                    else
                        aot = 0;

                var dp = document.getElementById("makedefaultmethod").checked;
                if (dp)
                        dp = 1;
                    else
                        dp = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : pmID, 'table' : table, 'key' : "payment_method_id", payment_method_name:desc, tender_amount:tender, payment_type:type, credentials_required:cr,
                        allow_over_tender:aot, default_method:dp},
                    success:function(data){
                        var x = document.getElementById("pm"+pmID);
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
        if (pmID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#desc").val("");
        $("#tender").val("");
        $("#cash").prop("checked", true);
        $("#check").prop("checked", false);
        $("#giftcertificate").prop("checked", false);
        $("#other").prop("checked", false);
        $("#credentialsrequired").prop("checked", false);
        $("#allowovertender").prop("checked", false);
        $("#makedefaultmethod").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM payment_method';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : pmID, 'table' : table, 'key' : "payment_method_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#paymentDropDown").html("<option value='' id='default' onclick=''>-Select Payment Method-</option>");
                var x = document.getElementById("paymentDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "pm"+JSONObject[0]["payment_method_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["payment_method_name"];
                    option.id = "pm" + JSONObject[i]["payment_method_id"];
                    x.add(option);
                    $("#paymentDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            pmID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            pmID = 0;
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
        const sql = 'SELECT * FROM payment_method WHERE payment_method_id = ' + pmID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : pmID, 'table' : table, 'key' : "payment_method_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#desc").val(JSONObject[0]["payment_method_name"]);
                $("#tender").val(JSONObject[0]["tender_amount"]);

                switch(parseInt(JSONObject[0]["payment_type"]))
                {
                    case 1:
                        $("#cash").prop("checked", true);
                        break;
                    case 2:
                        $("#check").prop("checked", true);
                        break;
                    case 3:
                        $("#giftcertificate").prop("checked", true);
                        break;
                    case 4:
                        $("#other").prop("checked", true);
                        break;
                }

                if (JSONObject[0]["credentials_required"] == 1)
                    $("#credentialsrequired").prop("checked", true);
                else
                    $("#credentialsrequired").prop("checked", false);

                if (JSONObject[0]["allow_over_tender"] == 1)
                    $("#allowovertender").prop("checked", true);
                else
                    $("#allowovertender").prop("checked", false);

                if (JSONObject[0]["default_method"] == 1)
                    $("#makedefaultmethod").prop("checked", true);
                else
                    $("#makedefaultmethod").prop("checked", false);
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