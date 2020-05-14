<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid">

        <div></div>
        <div style="display: flex;">
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
        </div>

        <div class="officePanelColumnLabel" style="text-align: right">Daily Sales</div>
        <div></div>

        <div class="officePanelGridLabel">Fiscal Day Start Time (hh:mm)</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Tabs</div>
        <div></div>

        <div class="officePanelGridLabel">Maximum Tab Amount</div>
        <div><input type="text" class="inputBox" style="width: 100px"></div>
        <div class="officePanelGridLabel">Tab Refresh Time (minutes)</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="automaticallyclosetabs" name="automaticallyclosetabs" value="automaticallyclosetabs">
        <label for="automaticallyclosetabs">Automatically close all open tabs at the end of the fiscal day</label></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Account with which to pay open tabs</div>
        <div>
            <select id="account" class="dropdownBox">
                <option value="account1">test account 1</option>
                <option value="account2">test account 2</option>
                <option value="account3">test account 3</option>
            </select>
        </div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Transaction History</div>
        <div></div>

        <div class="officePanelGridLabel">Number of days to save the Transaction History</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Lane Auto-Shutdown</div>
        <div></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="allowautoshutdown" name="allowautoshutdown" value="allowautoshutdown">
        <label for="allowautoshutdown">Allow use of Lane Auto-Shutdown mode (post-paid game/time limits)</label></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">Euro Currency</div>
        <div></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="showeurocurrency" name="showeurocurrency" value="showeurocurrency">
        <label for="showeurocurrency">Show Euro Currency Totals</label></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelGridLabel">Euro Currency Conversion Value</div>
        <div><input type="text" class="inputBox" style="width: 75px"></div>
    </div>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div class="officePanelGrid">
        <div class="officePanelColumnLabel" style="text-align: right">End Shift</div>
        <div></div>
        <div class="officePanelGridLabel">Shift Report Printer</div>
        <div><input type="text" class="inputBox"><button class="submitBtn" style="height: 30px">Browse</button></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="autoprintshift" name="autoprintshift" value="autoprintshift" checked>
        <label for="autoprintshift">Automatically print the Shift Report when shift ends</label></div>
    </div>

    <div class="officePanelGrid" style="grid-template-columns: 100%">
        <div><input type="checkbox" id="autocloseshifts" name="autocloseshifts" value="autocloseshifts" checked>
        <label for="autocloseshifts">Automatically close all shifts at the end of the fiscal day</label></div>
    </div>

</div>