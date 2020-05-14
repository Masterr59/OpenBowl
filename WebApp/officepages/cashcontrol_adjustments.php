<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="adjustments" class="dropdownBox">
                <option value="test1">test adjustment 1</option>
                <option value="test2">test adjustment 2</option>
                <option value="test3">test adjustment 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Adjustment Description</div>
        <div><input type="text" class="inputBox"></div>
        
        <div class="officePanelGridLabel">Item Adustment</div>
        <div>
            <input type="checkbox" id="yesitemadjustment" name="yesitemadjustment" value="yesitemadjustment">
            <label for="yesitemadjustment">Yes (If not checked, this item will be a transaction adjustment)</label>
        </div>

        <div></div>
        <select id="itemadjustments" class="dropdownBox" style="width: 200px">
            <option value="ppd">Preset Percentage Discount</option>
            <option value="pps">Preset Percentage Surcharge</option>
            <option value="pmd">Preset Monetary Discount</option>
            <option value="pms">Preset Monetary Surcharge</option>
            <option value="opd">Open Percentage Discount</option>
            <option value="ops">Open Percentage Surcharge</option>
            <option value="omd">Open Monetary Discount</option>
            <option value="oms">Open Monetary Surcharge</option>
        </select>

        <div class="officePanelGridLabel">Adjustment Percentage</div>
        <div><input type="text" class="inputBox" value="0.000" style="width: 100px">10 = 10.000%</div>
    </div>
</div>