<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">
        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>

        <div class="officePanelColumnLabel" style="text-align: right">Receipt Header Message</div>
        <div></div>
        <div class="officePanelGridLabel">Line 1</div>
        <div><input type="text" class="inputBox" value="Welcome To" id="a"></div>
        <div class="officePanelGridLabel">Line 2</div>
        <div><input type="text" class="inputBox" value="NORTH BOWL" id="b"></div>
        <div class="officePanelGridLabel">Line 3</div>
        <div><input type="text" class="inputBox" value="" id="c"></div>
        <div class="officePanelGridLabel">Line 4</div>
        <div><input type="text" class="inputBox" value="We Appreciate" id="d"></div>
        <div class="officePanelGridLabel">Line 5</div>
        <div><input type="text" class="inputBox" value="Your Business!" id="e"></div>
        
        <div class="officePanelColumnLabel" style="text-align: right">Receipt Footer Message</div>
        <div></div>
        <div class="officePanelGridLabel">Line 1</div>
        <div><input type="text" class="inputBox" value="Christmas is almost here" id="f"></div>
        <div class="officePanelGridLabel">Line 2</div>
        <div><input type="text" class="inputBox" value="Schedule Christmas parties Now!" id="g"></div>
        <div class="officePanelGridLabel">Line 3</div>
        <div><input type="text" class="inputBox" value="Inquire At the Desk" id="h"></div>
        <div class="officePanelGridLabel">Line 4</div>
        <div><input type="text" class="inputBox" value="" id="i"></div>
        <div class="officePanelGridLabel">Line 5</div>
        <div><input type="text" class="inputBox" value="" id="j"></div>

        <div class="officePanelColumnLabel" style="text-align: right">Receipt Labels</div>
        <div></div>
        <div class="officePanelGridLabel">Tax Total</div>
        <div><input type="text" class="inputBox" value="Tax Total" id="k"></div>
        <div class="officePanelGridLabel">Line Item Total</div>
        <div><input type="text" class="inputBox" value="Total" id="l"></div>
        <div class="officePanelGridLabel">Paid</div>
        <div><input type="text" class="inputBox" value="Paid" id="m"></div>
        <div class="officePanelGridLabel">Change</div>
        <div><input type="text" class="inputBox" value="Change" id="n"></div>
        <div class="officePanelGridLabel">Due</div>
        <div><input type="text" class="inputBox" value="Due" id="o"></div>
        <div class="officePanelGridLabel">Payment Total</div>
        <div><input type="text" class="inputBox" value="Payment Total" id="p"></div>
        <div class="officePanelGridLabel">Charge Total</div>
        <div><input type="text" class="inputBox" value="Charge Total" id="q"></div>
        <div class="officePanelGridLabel">Tender Total</div>
        <div><input type="text" class="inputBox" value="Tender Total" id="r"></div>

        <div></div>
        <div>
            <input type="checkbox" id="showpackageitemprices" name="showpackageitemprices" value="showpackageitemprices">
            <label for="showpackageitemprices">Show Package Item Prices</label>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    const table = "customer_receipt";
    loadFields();
    $("#edit").on('click', function(){
        var a = $("#a").val();
        var b = $("#b").val();
        var c = $("#c").val();
        var d = $("#d").val();
        var e = $("#e").val();
        var f = $("#f").val();
        var g = $("#g").val();
        var h = $("#h").val();
        var i = $("#i").val();
        var j = $("#j").val();
        var k = $("#k").val();
        var l = $("#l").val();
        var m = $("#m").val();
        var n = $("#n").val();
        var o = $("#o").val();
        var p = $("#p").val();
        var q = $("#q").val();
        var r = $("#r").val();

        var spip = document.getElementById("showpackageitemprices").checked;
        if (spip)
            spip = 1;
        else
            spip = 0;
        
        var ajaxRequest = {
            url: './update.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "customer_receipt_id", header_line_1:a,
                    header_line_2:b, header_line_3:c, header_line_4:d, header_line_5:e,
                    footer_line_1:f, footer_line_2:g, footer_line_3:h, footer_line_4:i,
                    footer_line_5:j, tax_total:k, line_item_total:l, paid:m, change_label:n,
                    due:o, payment_total:p, charge_total:q, tender_total:r, show_package_item_prices: spip},
            success:function(data){
                displayMsg("Successfully updated the details for Customer Receipts", 2);
            },
            error:function(data){
                displayMsg("An error occurred when updating the details for Customer Receipts", 1);
            }
        }
        $.ajax(ajaxRequest);
    });

    $("#undo").on('click', function(){
        loadFields();
    });

    function loadFields()
    {
        const sql = 'SELECT * FROM customer_receipt WHERE customer_receipt_id = 1';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "customer_receipt_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#a").val(JSONObject[0]["header_line_1"]);
                $("#b").val(JSONObject[0]["header_line_2"]);
                $("#c").val(JSONObject[0]["header_line_3"]);
                $("#d").val(JSONObject[0]["header_line_4"]);
                $("#e").val(JSONObject[0]["header_line_5"]);
                $("#f").val(JSONObject[0]["footer_line_1"]);
                $("#g").val(JSONObject[0]["footer_line_2"]);
                $("#h").val(JSONObject[0]["footer_line_3"]);
                $("#i").val(JSONObject[0]["footer_line_4"]);
                $("#j").val(JSONObject[0]["footer_line_5"]);
                $("#k").val(JSONObject[0]["tax_total"]);
                $("#l").val(JSONObject[0]["line_item_total"]);
                $("#m").val(JSONObject[0]["paid"]);
                $("#n").val(JSONObject[0]["change_label"]);
                $("#o").val(JSONObject[0]["due"]);
                $("#p").val(JSONObject[0]["payment_total"]);
                $("#q").val(JSONObject[0]["charge_total"]);
                $("#r").val(JSONObject[0]["tender_total"]);

                if (JSONObject[0]["show_package_item_prices"]==1)
                    $("#showpackageitemprices").prop("checked", true);
                else
                    $("#showpackageitemprices").prop("checked", false);
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