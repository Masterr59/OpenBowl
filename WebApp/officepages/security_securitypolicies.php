<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">
        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>
        <div class="officePanelGridLabel">Security devices installed at center</div>
        <div>
            <input type="checkbox" id="cardreaders" name="securitydevice" value="cardreaders">
            <label for="cardreaders">Card readers</label>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Password rules</div>
        <div>
            <input type="checkbox" id="resetpasswordfirstlogin" name="passwordrules" value="resetpasswordfirstlogin">
            <label for="resetpasswordfirstlogin">Reset password at first login</label><br/>
            <input type="checkbox" id="enforcepasswordhistory" name="passwordrules" value="enforcepasswordhistory">
            <label for="enforcepasswordhistory">Enforce password history</label><br/>
            <input type="checkbox" id="enforcestrongerpasswords" name="passwordrules" value="enforcestrongerpasswords">
            <label for="enforcestrongerpasswords">Enforce strong passwords</label>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Maximum days a user password stays active</div>
        <div>
            <select id="dayspassactive" class="dropdownBox">
                <option value="always">Always</option>
                <option value="days30">30</option>
                <option value="days60">60</option>
                <option value="days90">90</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Maximum days of inactivity a user account stays active</div>
        <div>
            <select id="daysinactivity" class="dropdownBox">
                <option value="always">Always</option>
                <option value="days30">30</option>
                <option value="days60">60</option>
                <option value="days90">90</option>
            </select>
        </div>
        <div class="officePanelGridLabel">Maximum failed access attempts before user is locked out</div>
        <div>
            <select id="dayspassactive" class="dropdownBox">
                <option value="unlimited">Unlimited</option>
                <option value="attempts1">1</option>
                <option value="attempts2">2</option>
                <option value="attempts3">3</option>
                <option value="attempts4">4</option>
                <option value="attempts5">5</option>
            </select>
        </div>
    </div>
    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>
    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Inactive OpenBowl timeout (Minutes)</div>
        <div><input type="text" class="inputBox" style="width: 40px"></div>
    </div>
</div>