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
        <div class="officePanelGridLabel">Description</div>
        <div><input type="text" class="inputBox"></div>
        <div></div>
        <div>
            <input type="checkbox" id="monday" name="day" value="monday">
            <label for="monday">Monday</label><br/>
            <input type="checkbox" id="tuesday" name="day" value="tuesday">
            <label for="tuesday">Tuesday</label><br/>
            <input type="checkbox" id="wednesday" name="day" value="wednesday">
            <label for="wednesday">Wednesday</label><br/>
            <input type="checkbox" id="thursday" name="day" value="thursday">
            <label for="thursday">Thursday</label><br/>
            <input type="checkbox" id="friday" name="day" value="friday">
            <label for="friday">Friday</label><br/>
            <input type="checkbox" id="saturday" name="day" value="saturday">
            <label for="saturday">Saturday</label><br/>
            <input type="checkbox" id="sunday" name="day" value="sunday">
            <label for="sunday">Sunday</label>
        </div>
        <div class="officePanelGridLabel">Starting Time (hh:mm)</div>
        <div><input type="text" class="inputBox" value="00:00"></div>
        <div class="officePanelGridLabel">Ending Time (hh:mm)</div>
        <div><input type="text" class="inputBox" value="00:00"></div>
    </div>
</div>