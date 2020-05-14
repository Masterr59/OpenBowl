<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="modifiers" class="dropdownBox">
                <option value="test1">test modifier 1</option>
                <option value="test2">test modifier 2</option>
                <option value="test3">test modifier 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Modifier Description</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Modifier Category</div>
        <div><input type="text" class="inputBox"></div>
        <div class="officePanelGridLabel">Modifier Default Price</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
    </div>
</div>