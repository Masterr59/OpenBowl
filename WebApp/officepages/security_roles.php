<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="roles" class="dropdownBox">
                <option value="test1">test role 1</option>
                <option value="test2">test role 2</option>
                <option value="test3">test role 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Role Name</div>
        <div>
            <input type="text" class="inputBox"><br/>
            <input type="checkbox" id="allowcaptionediting" name="rolename" value="allowcaptionediting">
            <label for="allowcaptionediting">Allow caption editing</label><br/>
            <input type="checkbox" id="preventtabviewing" name="rolename" value="preventtabviewing">
            <label for="preventtabviewing">Prevent tab and gratuity viewing of other waitstaff</label>
        </div>
        <div class="officePanelGridLabel">Login Mode</div>
        <div>
            <input type="checkbox" id="cantypeloginname" name="loginmode" value="cantypeloginname" checked>
            <label for="cantypeloginname">Can type login name and password (set by default)</label><br/>
            <input type="checkbox" id="canswipecard" name="loginmode" value="canswipecard" disabled>
            <label for="canswipecard">Can swipe magnetic card</label>
        </div>
    </div>
</div>