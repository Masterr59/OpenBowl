<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select class="dropdownBox" id="usersDropDown"></select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
            <div class="saveBtn" id="print"><i class="fa fa-print"></i></div>
        </div>
        <div class="officePanelGridLabel">New Login Name</div>
        <div><input type="text" class="inputBox" id="login_name"></div>
        <div class="officePanelGridLabel">New Password</div>
        <div><input type="password" class="inputBox" id="password"></div>
        <div class="officePanelGridLabel">Confirm New Password</div>
        <div><input type="password" class="inputBox" id="password_confirmation"></div>
        <div class="officePanelGridLabel">Full User Name</div>
        <div><input type="text" class="inputBox" id="full_user_name"></div>
        <div class="officePanelGridLabel">Waitstaff Code</div>
        <div><input type="text" class="inputBox" style="width: 75px" id="waitstaff_code"></div>
        <div class="officePanelGridLabel">Language</div>
        <div>
            <select id="language" class="dropdownBox" id="languagesDropDown">
                <option value="language1">English</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Security Role</div>
        <div>
            <select id="rolesDropDown" class="dropdownBox"></select>
        </div>
        <div class="officePanelGridLabel">E-Mail</div>
        <div><input type="text" class="inputBox" id="email"></div>
        <div class="officePanelGridLabel">Phone</div>
        <div><input type="text" class="inputBox" id="phone"></div>
        <div class="officePanelGridLabel">Address 1</div>
        <div><input type="text" class="inputBox" id="address1"></div>
        <div class="officePanelGridLabel">Address 2</div>
        <div><input type="text" class="inputBox" id="address2"></div>
        <div class="officePanelGridLabel">Address 3</div>
        <div><input type="text" class="inputBox" id="address3"></div>
        <div class="officePanelGridLabel">Address 4</div>
        <div><input type="text" class="inputBox" id="address4"></div>
        <div class="officePanelGridLabel"></div>
        <div>
            <input type="checkbox" id="inactive_user" name="inactiveuser" value="inactiveuser">
            <label for="inactiveuser">Inactive User</label>
        </div>
        <div class="officePanelGridLabel"></div>
        <div>
            <input type="checkbox" id="reset_pass" name="resetpasswordnextlogin" value="resetpasswordnextlogin">
            <label for="resetpasswordnextlogin">Reset password at next login</label>
        </div>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var userID = 0;
    var roleID = 0;
    const table = "user";
    reloadDropdown();
    getRoles();
    $('#new').on('click', function(){
        if (checkValidData())
        {
            const login_name = $("#login_name").val();
            const password = $("#password").val();
            const password_confirmation = $("#password_confirmation").val();
            const full_user_name = $("#full_user_name").val();
            const waitstaff_code = $("#waitstaff_code").val();
            const email = $("#email").val();
            const phone = $("#phone").val();
            const address1 = $("#address1").val();
            const address2 = $("#address2").val();
            const address3 = $("#address3").val();
            const address4 = $("#address4").val();
            var inactive_user = document.getElementById("inactive_user").checked;
            var reset_pass = document.getElementById("reset_pass").checked;

            if (inactive_user)
                inactive_user = 1;
            else
                inactive_user = 0;

            if (reset_pass)
                reset_pass = 1;
            else
                reset_pass = 0;

            var ajaxRequest = {
                url: './submit.php',
                type: 'POST',
                data: {table:table, username:login_name, user_role_id:roleID, full_user_name:full_user_name, waitstaff_code:waitstaff_code, email:email,
                        phone:phone, address1:address1, address2:address2, address3:address3, address4:address4, inactive_user:inactive_user,
                        reset_password_next_login:reset_pass},
                success:function(data){
                    reloadDropdown();
                    userID = 0;
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
        if (userID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'id' : userID, 'table' : table, 'key' : "user_id"},
                success:function(data){
                    clearForm();
                    displayMsg("Successfully deleted the selected record.", 2);
                    var selectedDropDownItem = document.getElementById("usersDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                    userID = 0;
                },
                error:function(data){
                    displayMsg("An error occurred when deleting the selected record.", 1);
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#edit").on('click', function(){
        if (userID != 0)
        {
            if (checkValidData())
            {
                const login_name = $("#login_name").val();
                const password = $("#password").val();
                const password_confirmation = $("#password_confirmation").val();
                const full_user_name = $("#full_user_name").val();
                const waitstaff_code = $("#waitstaff_code").val();
                const email = $("#email").val();
                const phone = $("#phone").val();
                const address1 = $("#address1").val();
                const address2 = $("#address2").val();
                const address3 = $("#address3").val();
                const address4 = $("#address4").val();
                var inactive_user = document.getElementById("inactive_user").checked;
                var reset_pass = document.getElementById("reset_pass").checked;

                if (inactive_user)
                    inactive_user = 1;
                else
                    inactive_user = 0;

                if (reset_pass)
                    reset_pass = 1;
                else
                    reset_pass = 0;
                
                var ajaxRequest = {
                    url: './update.php',
                    type: 'POST',
                    data: { 'id' : userID, 'table' : table, 'key' : "user_id", username:login_name, user_role_id:roleID, full_user_name:full_user_name, waitstaff_code:waitstaff_code, email:email,
                        phone:phone, address1:address1, address2:address2, address3:address3, address4:address4, inactive_user:inactive_user,
                        reset_password_next_login:reset_pass},
                    success:function(data){
                        var x = document.getElementById("user"+userID);
                        x.text = login_name;
                        displayMsg("Successfully updated the details for " + login_name + ".", 2);
                    },
                    error:function(data){
                        displayMsg("An error occurred when updating the details for " + login_name + ".", 1);
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
        if (userID != 0)
            loadFields();
    });

    function clearForm()
    {
        $("#login_name").val("");
        $("#password").val("");
        $("#password_confirmation").val("");
        $("#full_user_name").val("");
        $("#waitstaff_code").val("");
        $("#email").val("");
        $("#phone").val("");
        $("#address1").val("");
        $("#address2").val("");
        $("#address3").val("");
        $("#address4").val("");
        $("#inactive_user").prop("checked", false);
        $("#reset_password_next_login").prop("checked", false);
        roleID = 0;
    }

    function reloadDropdown()
    {
        const sql = 'SELECT * FROM user';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : userID, 'table' : table, 'key' : "user_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#usersDropDown").html("<option value='' id='default' onclick=''>-Select User-</option>");
                var x = document.getElementById("usersDropDown");
                for (var i = 0; i < JSONObject.length; i++)
                {
                    var cur = "user"+JSONObject[0]["user_id"];
                    
                    var option = document.createElement("option");
                    option.text = JSONObject[i]["username"];
                    option.id = "user" + JSONObject[i]["user_id"];
                    x.add(option);
                    $("#usersDropDown option").click(function(){
                        const s = "#" + $(this).attr('id');
                        if (s != "#default")
                        {
                            var matches = s.match(/(\d+)/);
                            userID = parseInt(matches[0]);
                            loadFields();
                        }
                        else
                        {
                            userID = 0;
                            clearForm();
                            $('#rolesDropDown option:eq(0)').prop('selected', true);
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
        const sql = 'SELECT * FROM user WHERE user_id = ' + userID;
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : userID, 'table' : table, 'key' : "user_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                roleID = JSONObject[0]["user_role_id"];
                $("#login_name").val(JSONObject[0]["username"]);
                $("#full_user_name").val(JSONObject[0]["full_user_name"]);
                $("#waitstaff_code").val(JSONObject[0]["waitstaff_code"]);
                $("#email").val(JSONObject[0]["email"]);
                $("#phone").val(JSONObject[0]["phone"]);
                $("#address1").val(JSONObject[0]["address1"]);
                $("#address2").val(JSONObject[0]["address2"]);
                $("#address3").val(JSONObject[0]["address3"]);
                $("#address4").val(JSONObject[0]["address4"]);
                var k = "role"+roleID;
                $('#rolesDropDown option[id="'+k+'"]').attr("selected", "selected");
                if (JSONObject[0]["inactive_user"]==1)
                    $("#inactive_user").prop("checked", true);
                else
                    $("#inactive_user").prop("checked", false);

                if (JSONObject[0]["reset_password_next_login"]==1)
                    $("#reset_pass").prop("checked", true);
                else
                    $("#reset_pass").prop("checked", false);
            },
            error:function(JSONObject){
                displayMsg("Error: Load failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function getRoles()
    {
        const sql = 'SELECT * FROM user_role';
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'id' : userID, 'table' : table, 'key' : "user_role_id", 'sql' : sql },
            dataType: 'json',
            success:function(JSONObject){
                $("#rolesDropDown").html("<option value='' id='defaultRole' onclick=''>-Select Role-</option>");
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
                        if (s != "#defaultRole")
                        {
                            var matches = s.match(/(\d+)/);
                            roleID = parseInt(matches[0]);
                        }
                        else
                        {
                            roleID = 0;
                        }

                    });
                }

            },
            error:function(JSONObject){
                displayMsg("Error: Security Roles Reload Dropdown failed", 1);
            }
        }
        $.ajax(ajaxRequest);
    }

    function checkValidData()
    {
        const login_name = $("#login_name").val();
        const full_user_name = $("#full_user_name").val();
        const email = $("#email").val();
        const phone = $("#phone").val();
        const address = $("#address1").val();

        if (login_name == "" || full_user_name == "" || email == "" || phone == "" || address == "" || roleID == 0)
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