<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">
        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>
        <div class="officePanelGridLabel">Name of the Center</div>
        <div><input type="text" class="inputBox" id="name_of_center"></div>
        <div class="officePanelGridLabel">Name of the Owner</div>
        <div><input type="text" class="inputBox" id="name_of_owner"></div>
        <div class="officePanelGridLabel">Center's Contact Person</div>
        <div><input type="text" class="inputBox" id="contact_person"></div>
        <div class="officePanelGridLabel">Center Identification Number</div>
        <div><input type="text" class="inputBox" id="id_number"></div>
        <div class="officePanelGridLabel">Address</div>
        <div><input type="text" class="inputBox" id="address"></div>
        <div class="officePanelGridLabel">City</div>
        <div><input type="text" class="inputBox" id="city"></div>
        <div class="officePanelGridLabel">State/Province</div>
        <div><input type="text" class="inputBox" id="state"></div>
        <div class="officePanelGridLabel">Postal Code</div>
        <div><input type="text" class="inputBox" id="postal_code"></div>
        <div class="officePanelGridLabel">Telephone Number</div>
        <div><input type="text" class="inputBox" id="phone"></div>
        <div class="officePanelGridLabel">Fax Number</div>
        <div><input type="text" class="inputBox" id="fax"></div>
        <div class="officePanelGridLabel">Modem Number</div>
        <div><input type="text" class="inputBox" id="modem"></div>
        <div class="officePanelGridLabel">E-Mail</div>
        <div><input type="text" class="inputBox" id="email"></div>
        <div class="officePanelGridLabel">Number of Bowling Lanes</div>
        <div><input type="text" class="inputBox" id="lanes"></div>
        <div class="officePanelGridLabel">Scoring</div>
        <div>
            <input type="radio" id="framework" name="scoring" value="framework">
            <label for="framework">Framework</label>
            <input type="radio" id="openbowl" name="scoring" value="openbowl">
            <label for="framework">Open Bowl</label>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    const table = "center_information";
    loadFields();
    $("#edit").on('click', function(){
        var framework = document.getElementById("framework").checked;
        var openbowl = document.getElementById("openbowl").checked;
        var center = $("#name_of_center").val();
        var owner = $("#name_of_owner").val();
        var contact = $("#contact_person").val();
        var id_num = $("#id_number").val();
        var address = $("#address").val();
        var city = $("#city").val();
        var state = $("#state").val();
        var postal = $("#postal_code").val();
        var phone = $("#phone").val();
        var fax = $("#fax").val();
        var modem = $("#modem").val();
        var email = $("#email").val();
        var lanes = $("#lanes").val();
        var scoring = 0;

        if (framework)
            scoring = 0;
        else if (openbowl)
            scoring = 1;
        
        var ajaxRequest = {
            url: './update.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "center_id", center_name:center, owner_name:owner,
                    contact_person:contact, center_identification_number:id_num, address:address, city:city,
                    state:state, postal_code:postal, phone:phone, fax:fax, modem:modem, email:email, number_of_lanes:lanes,
                    scoring:scoring },
            success:function(data){
                displayMsg("Successfully updated the details for Center Information", 2);
            },
            error:function(data){
                displayMsg("An error occurred when updating the details for Center Information", 1);
            }
        }
        $.ajax(ajaxRequest);
    });

    $("#undo").on('click', function(){
        loadFields();
    });

    function loadFields()
    {
        const sql = 'SELECT * FROM center_information WHERE center_id = 1';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "center_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#name_of_center").val(JSONObject[0]["center_name"]);
                $("#name_of_owner").val(JSONObject[0]["owner_name"]);
                $("#contact_person").val(JSONObject[0]["contact_person"]);
                $("#id_number").val(JSONObject[0]["center_identification_number"]);
                $("#address").val(JSONObject[0]["address"]);
                $("#city").val(JSONObject[0]["city"]);
                $("#state").val(JSONObject[0]["state"]);
                $("#postal_code").val(JSONObject[0]["postal_code"]);
                $("#phone").val(JSONObject[0]["phone"]);
                $("#fax").val(JSONObject[0]["fax"]);
                $("#modem").val(JSONObject[0]["modem"]);
                $("#email").val(JSONObject[0]["email"]);
                $("#lanes").val(JSONObject[0]["number_of_lanes"]);

                if (JSONObject[0]["scoring"] == 0)
                    $("#framework").prop("checked", true);
                else
                    $("#openbowl").prop("checked", true);

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