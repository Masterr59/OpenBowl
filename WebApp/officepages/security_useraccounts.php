<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="logins" class="dropdownBox">
                <option value="test1">Firstname - test login 1</option>
                <option value="test2">Firstname - test login 2</option>
                <option value="test3">Firstname - test login 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
            <div class="saveBtn" id="print"><i class="fa fa-print"></i></div>
        </div>
        <div class="officePanelGridLabel">New Login Name</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">New Password</div>
        <div><input type="password" class="inputBox"></div>
        <div class="officePanelGridLabel">Confirm New Password</div>
        <div><input type="password" class="inputBox"></div>
        <div class="officePanelGridLabel">Full User Name</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Waitstaff Code</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
        <div class="officePanelGridLabel">Language</div>
        <div>
            <select id="language" class="dropdownBox">
                <option value="language1">English</option>
                <option value="language2">Spanish</option>
                <option value="language3">French</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Security Role</div>
        <div>
            <select id="securityrole" class="dropdownBox">
                <option value="role1">Administrator</option>
                <option value="role2">role 2</option>
                <option value="role3">role 3</option>
            </select>
        </div>
        <div class="officePanelGridLabel">E-Mail</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Phone</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Address 1</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel"></div>
        <div>
            <input type="checkbox" id="inactiveuser" name="inactiveuser" value="inactiveuser">
            <label for="inactiveuser">Inactive User</label>
        </div>
        <div class="officePanelGridLabel"></div>
        <div>
            <input type="checkbox" id="resetpasswordnextlogin" name="resetpasswordnextlogin" value="resetpasswordnextlogin">
            <label for="resetpasswordnextlogin">Reset password at next login</label>
        </div>
    </div>
</div>