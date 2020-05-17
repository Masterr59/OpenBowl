<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="rolesDropDown" class="dropdownBox"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Role Name</div>
        <div>
            <input type="text" class="inputBox" id="role_name"><br/>
            <input type="checkbox" id="allowcaptionediting" name="rolename" value="allowcaptionediting">
            <label for="allowcaptionediting">Allow caption editing</label><br/>
            <input type="checkbox" id="preventtabviewing" name="rolename" value="preventtabviewing">
            <label for="preventtabviewing">Prevent tab and gratuity viewing of other waitstaff</label>
        </div>
        <div class="officePanelGridLabel">Login Mode</div>
        <div>
            <input type="checkbox" id="cantypeloginname" name="loginmode" value="cantypeloginname">
            <label for="cantypeloginname">Can type login name and password (set by default)</label><br/>
            <input type="checkbox" id="canswipecard" name="loginmode" value="canswipecard" disabled>
            <label for="canswipecard">Can swipe magnetic card</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div style="margin-top: 10px;">
        <b>Users</b> associated with this Role<br/>
        
        <div style="margin-top: 10px;">
            <table class="officePanelTable" style="margin-bottom: 10px;">
                <thead>
                    <tr>
                        <th>Username</th>
                    </tr>
                </thead>
                <tbody id="userTable">
                    
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var roleID = 0;
    const table = "user_role";
    reloadDropdown();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const role_name = $("#role_name").val();
            var caption_editing = document.getElementById("allowcaptionediting").checked;
            var prevent_tab_viewing = document.getElementById("preventtabviewing").checked;
            var can_type_login_name = document.getElementById("cantypeloginname").checked;
            var can_swipe_card = document.getElementById("canswipecard").checked;

            if (caption_editing)
                caption_editing = 1;
            else
                caption_editing = 0;

            if (prevent_tab_viewing)
                prevent_tab_viewing = 1;
            else
                prevent_tab_viewing = 0;

            if (can_type_login_name)
                can_type_login_name = 1;
            else
                can_type_login_name = 0;

            if (can_swipe_card)
                can_swipe_card = 1;
            else
                can_swipe_card = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, user_role_name:role_name, allow_caption_editing:caption_editing, prevent_tab_gratuity_viewing:prevent_tab_viewing,
                        can_type_name_password:can_type_login_name, can_swipe_magnetic_card:can_swipe_card},
                success:function(data){
                    reloadDropdown();
                    roleID = 0;
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
        if (roleID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : roleID, 'table' : table, 'key' : "user_role_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("rolesDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    roleID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (roleID != 0)
        {
            if (checkValidData())
            {
                const role_name = $("#role_name").val();
                var caption_editing = document.getElementById("allowcaptionediting").checked;
                var prevent_tab_viewing = document.getElementById("preventtabviewing").checked;
                var can_type_login_name = document.getElementById("cantypeloginname").checked;
                var can_swipe_card = document.getElementById("canswipecard").checked;

                if (caption_editing)
                    caption_editing = 1;
                else
                    caption_editing = 0;

                if (prevent_tab_viewing)
                    prevent_tab_viewing = 1;
                else
                    prevent_tab_viewing = 0;

                if (can_type_login_name)
                    can_type_login_name = 1;
                else
                    can_type_login_name = 0;

                if (can_swipe_card)
                    can_swipe_card = 1;
                else
                    can_swipe_card = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : roleID, 'table' : table, 'key' : "user_role_id", user_role_name:role_name, allow_caption_editing:caption_editing, prevent_tab_gratuity_viewing:prevent_tab_viewing,
                        can_type_name_password:can_type_login_name, can_swipe_magnetic_card:can_swipe_card},
                    success:function(data){
                        var x = document.getElementById("role"+roleID);
                        x.text = role_name;
                        displayMsg("Successfully updated the details for " + role_name + ".", 2);
                    },
                    error:function(data){
                        displayMsg("An error occurred when updating the details for " + role_name + ".", 1);
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
        if (roleID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#role_name").val("");
        $("#caption_editing").prop("checked", false);
        $("#prevent_tab_viewing").prop("checked", false);
        $("#can_type_login_name").prop("checked", true);
        $("#can_swipe_card").prop("checked", false);
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM user_role';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : roleID, 'table' : table, 'key' : "user_role_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#rolesDropDown").html("<option value='' id='default' onclick=''>-Select Role-</option>");
                var x = document.getElementById("rolesDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "role"+JSONObject[0]["user_role_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["user_role_name"];
                    option.id = "role" + JSONObject[i]["user_role_id"];
                    x.add(option);
                    $("#rolesDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            roleID = parseInt(matches[0]);

                            loadFields();
                        }
                        else
                        {
                            roleID = 0;
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
        const sql = 'SELECT * FROM user_role WHERE user_role_id = ' + roleID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : roleID, 'table' : table, 'key' : "user_role_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#role_name").val(JSONObject[0]["user_role_name"]);

                if (JSONObject[0]["allow_caption_editing"]==1)
                    $("#allowcaptionediting").prop("checked", true);
                else
                    $("#allowcaptionediting").prop("checked", false);

                if (JSONObject[0]["prevent_tab_gratuity_viewing"]==1)
                    $("#preventtabviewing").prop("checked", true);
                else
                    $("#preventtabviewing").prop("checked", false);

                if (JSONObject[0]["can_type_name_password"]==1)
                    $("#cantypeloginname").prop("checked", true);
                else
                    $("#cantypeloginname").prop("checked", false);

                if (JSONObject[0]["can_swipe_magnetic_card"]==1)
                    $("#canswipecard").prop("checked", true);
                else
                    $("#canswipecard").prop("checked", false);
                getUsers();
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function getUsers()
    {
        const sql = 'SELECT * FROM user WHERE user_role_id = ' + roleID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : roleID, 'table' : "user", 'key' : "user_role_id", 'sql' : sql},
            dataType: 'json',
            success:function(JSONObject){
                $("#userTable").html("");
                for(var i = 0; i < JSONObject.length; i++)
                {
                    $("#userTable").append("<tr><td>" + JSONObject[i]["username"] +"</td></tr>");
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
        const data = $("#role_name").val();
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