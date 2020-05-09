<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 50px 1fr">
        <div></div>
        <div>
            <button type="submit" class="saveBtn"><i class="fa fa-save"></i></button>
        </div>
        <div></div>
        <div>
            <input type="checkbox" id="enabled" name="enabled" value="enabled" checked>
            <label for="enabled">Enabled</label>
        </div>
        <div class="officePanelGridLabel">F1</div>
        <div><input type="text" class="inputBox" value="Pop Revenue"></div>
        <div class="officePanelGridLabel">Product</div>
        <div>
            <select id="products" class="dropdownBox">
                <option value="test1">test product 1</option>
                <option value="test2">test product 2</option>
                <option value="test3">test product 3</option>
            </select>
            <input type="checkbox" id="usepackage" name="usepackage" value="usepackage">
            <label for="usepackage">Use Package</label>
        </div>
        <div class="officePanelGridLabel">Rate</div>
        <select id="rates" class="dropdownBox">
            <option value="test1">test rate 1</option>
            <option value="test2">test rate 2</option>
            <option value="test3">test rate 3</option>
        </select>
    </div>
</div>