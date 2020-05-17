<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">
        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>
        <div class="officePanelColumnLabel" style="text-align: center">Adjustment Activity</div>
        <div class="officePanelColumnLabel">Message Posted to Transaction</div>
    </div>
    
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">User selects the Cancel button at desk</div>
        <div><input type="text" class="inputBox" id="a"></div>
        <div class="officePanelGridLabel">User selects the Undo button when separating tabs</div>
        <div><input type="text" class="inputBox" id="b"></div>
        <div class="officePanelGridLabel">User cancels active transactions in OB Office</div>
        <div><input type="text" class="inputBox" id="c"></div>
        <div class="officePanelGridLabel">User marks active transactions paid in OB Office</div>
        <div><input type="text" class="inputBox" id="d"></div>
        <div class="officePanelGridLabel">User cancels old tabs during end shift</div>
        <div><input type="text" class="inputBox" id="e"></div>
        <div class="officePanelGridLabel">User marks old tabs paid during end shift</div>
        <div><input type="text" class="inputBox" id="f"></div>
        <div class="officePanelGridLabel">System automatically cancels problem transactions</div>
        <div><input type="text" class="inputBox" id="g"></div>
        <div class="officePanelGridLabel">System automatically closes open transactions</div>
        <div><input type="text" class="inputBox" id="h"></div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    const table = "adjustment_message";
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
        
        var ajaxRequest = {
            url: './update.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "adjustment_message_id", user_selects_cancel:a,
                    user_selects_undo:b, user_cancels_active:c, user_marks_active:d, user_cancels_old_tabs:e,
                    user_marks_old_tabs:f, system_cancels:g, system_closes:h},
            success:function(data){
                displayMsg("Successfully updated the details for Adjustment Message", 2);
            },
            error:function(data){
                displayMsg("An error occurred when updating the details for Adjustment Message", 1);
            }
        }
        $.ajax(ajaxRequest);
    });

    $("#undo").on('click', function(){
        loadFields();
    });

    function loadFields()
    {
        const sql = 'SELECT * FROM adjustment_message WHERE adjustment_message_id = 1';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : 1, 'table' : table, 'key' : "adjustment_message_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#a").val(JSONObject[0]["user_selects_cancel"]);
                $("#b").val(JSONObject[0]["user_selects_undo"]);
                $("#c").val(JSONObject[0]["user_cancels_active"]);
                $("#d").val(JSONObject[0]["user_marks_active"]);
                $("#e").val(JSONObject[0]["user_cancels_old_tabs"]);
                $("#f").val(JSONObject[0]["user_marks_old_tabs"]);
                $("#g").val(JSONObject[0]["system_cancels"]);
                $("#h").val(JSONObject[0]["system_closes"]);
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