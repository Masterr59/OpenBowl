<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="taxes" class="dropdownBox">
                <option value="test1">test tax 1</option>
                <option value="test2">test tax 2</option>
                <option value="test3">test tax 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Tax Number</div>
        <div id="taxnumber">0</div>
        <div class="officePanelGridLabel">Tax Description</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartments" class="dropdownBox">
            <option value="test1">test subdptment 1</option>
            <option value="test2">test subdptment 2</option>
            <option value="test3">test subdptment 3</option>
        </select>
        <div class="officePanelGridLabel">Tax Amount (7 = 7.000%)</div>
        <div><input type="text" class="inputBox" value="0"></div>
        <div class="officePanelGridLabel">Transaction Tax</div>
        <div>
            <input type="checkbox" id="yestransactiontax" name="yestransactiontax" value="yestransactiontax">
            <label for="yestransactiontax">Yes</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div>
        Products Using This Tax<br/>
        <b>Table will be here</b>
    </div>
</div>