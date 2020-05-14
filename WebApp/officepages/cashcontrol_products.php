<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="products" class="dropdownBox">
                <option value="test1">test product 1</option>
                <option value="test2">test product 2</option>
                <option value="test3">test product 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div></div>
        <div>
            <input type="radio" id="standard" name="standard" value="standard">
            <label for="standard">Standard</label><br/>
            <input type="radio" id="10pin" name="standard" value="10pin">
            <label for="10pin">Game Bowling</label><br/>
            <input type="radio" id="5pin" name="standard" value="5pin">
            <label for="5pin">Time Bowling</label>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel">Description</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Stock Keeping Unit (SKU)</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Sub-Department</div>
        <select id="subdepartments" class="dropdownBox">
            <option value="test1">test subdptment 1</option>
            <option value="test2">test subdptment 2</option>
            <option value="test3">test subdptment 3</option>
        </select>
        <div class="officePanelGridLabel">Cash Register Order</div>
        <div>
            <input type="text" class="inputBox" style="width: 50px">
            <input type="checkbox" id="outofstock" name="outofstock" value="outofstock">
            <label for="outofstock">Out of Stock</label>
        </div>
        <div class="officePanelGridLabel">Price</div>
        <div>
            <input type="text" class="inputBox" style="width: 75px">
            <input type="checkbox" id="alwaysopenprice" name="alwaysopenprice" value="alwaysopenprice">
            <label for="alwaysopenprice">Always Open price</label>
        </div>
        <div class="officePanelGridLabel">Sold By</div>
        <div>
            <input type="radio" id="perunit" name="soldby" value="perunit">
            <label for="perunit">Per Unit</label>
            <input type="radio" id="perhour" name="soldby" value="perhour">
            <label for="perhour">Per Hour</label>
        </div>
        <div></div>
        <div>
            <input type="checkbox" id="includetax" name="includetax" value="includetax">
            <label for="includetax">Include Tax</label><br/>
            <input type="checkbox" id="enablerates" name="enablerates" value="enablerates">
            <label for="enablerates">Rates - Enable</label><br/>
            <input type="checkbox" id="enablemodifiers" name="enablemodifiers" value="enablemodifiers">
            <label for="enablemodifiers">Modifiers - Enable</label>
        </div>
    </div>
</div>