<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div class="officePanelGrid" style="grid-template-columns: 160px 1fr">
  <div class="officePanelGridLabel">Sub-Department</div>
  <select id="subdepartments" class="dropdownBox">
    <option value="test1">test subdptment 1</option>
    <option value="test2">test subdptment 2</option>
    <option value="test3">test subdptment 3</option>
  </select>
</div>

<div>
    <div>
        <table class="officePanelTable" style="margin-bottom: 10px;">
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Rates</th>
                <th>Sold By</th>
                <th>Type</th>
                <th>Disabled</th>
              </tr>
            </thead>
            <tbody id="transactionTable1">
              <!-- transactionTable1 Data -->
            </tbody>
          </table>
    </div>
</div>

<div class="separator">
    <div class="topline" style="width: 100%"></div>
    <div class="bottomline" style="width: 100%"></div>
</div>

<div class="officePanelGrid" style="grid-template-columns: 160px 1fr; margin: 5px 0px 10px 0px;">
    <div class="officePanelGridLabel">Amount to adjust price by</div>
    <div><input type="text" class="inputBox" style="width: 75px;" value="0"></div>

    <div></div>
    <div>
        <select id="incamount" class="dropdownBox">
            <option value="increase">Increase amount (+)</option>
            <option value="decrease">Decrease amount (-)</option>
        </select>
    </div>

    <div></div>
    <div>
        <select id="fixedamount" class="dropdownBox">
            <option value="fixed">Amount is fixed</option>
            <option value="percent">Amount is %</option>
        </select>
    </div>

    <div></div>
    <div><button type="submit" class="saveBtn"><i class="fa fa-save"></i></button></div>
</div>