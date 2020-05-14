<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="banking" class="dropdownBox">
                <option value="test1">test banking 1</option>
                <option value="test2">test banking 2</option>
                <option value="test3">test banking 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Banking Description</div>
        <div><input type="text" class="inputBox"></div>
        
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartments" class="dropdownBox">
            <option value="test1">test subdptment 1</option>
            <option value="test2">test subdptment 2</option>
            <option value="test3">test subdptment 3</option>
        </select>

        <div></div>
        <div>
            <input type="checkbox" id="payout" name="payout" value="payout">
            <label for="payout">Pay Out</label>
        </div>
    </div>
</div>