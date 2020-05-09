<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div class="officePanelGrid" style="grid-template-columns: 160px 1fr">
  <div class="officePanelGridLabel">Search for Date</div>
  <div style="display: flex; align-items: center;">
    <input type="text" class="inputBox" placeholder="mm/dd/yyyy">
    <button type="submit" class="saveBtn" style="margin-left: 5px;"><i class="fa fa-check-circle"></i></button>
  </div>
</div>

<div style="display: flex; flex-direction: column;">
    <div>
        <table class="officePanelTable" style="margin-bottom: 10px;">
            <thead>
              <tr>
                <th>Receipt</th>
                <th>Register</th>
                <th>Time</th>
                <th>Amount</th>
                <th>Type</th>
                <th>Extracted</th>
              </tr>
            </thead>
            <tbody id="transactionTable1">
              <tr>
                <td>2-0-333-0</td>
                <td>RESTAURANT</td>
                <td>10:31:57 PM</td>
                <td>$2.00</td>
                <td>Sales</td>
                <td>No</td>
              </tr>
            </tbody>
          </table>
    </div>
    <div class="officePanelGrid" style="grid-template-columns: 160px 1fr">
      <div class="officePanelGridLabel">Give reason for cancelling</div>
      <div style="display: flex; flex-wrap: wrap;">
        <input type="text" class="inputBox"><div class="bigButton" style="height: 27px; margin: 0px 0px 0px 5px;">Cancel Transaction</div>
      </div>
    </div>
    <div>
    <table class="officePanelTable" style="margin-bottom: 10px;">
        <thead>
          <tr>
            <th>Description</th>
            <th>Unit</th>
            <th>Total</th>
            <th>Quantity</th>
            <th>Sub Department</th>
            <th>Deleted</th>
            <th>Valid</th>
          </tr>
        </thead>
        <tbody id="transactionTable2">

        </tbody>
      </table>
    </div>
    <div class="officePanelGrid" style="grid-template-columns: 160px 1fr">
      <div class="officePanelGridLabel" style="margin-top: 6px">Sub-Department</div>
      <div style="display: flex; align-items: center;">
        <select id="subdepartments" class="dropdownBox">
            <option value="test1">test subdptment 1</option>
            <option value="test2">test subdptment 2</option>
            <option value="test3">test subdptment 3</option>
        </select>
        <button type="submit" class="saveBtn" style="margin-left: 5px;"><i class="fa fa-save"></i></button>
      </div>

      <div class="officePanelGridLabel">Quantity</div>
      <div><input type="text" class="inputBox"></div>

      <div class="officePanelGridLabel">Unit Amount</div>
      <div><input type="text" class="inputBox"></div>

      <div class="officePanelGridLabel">Total</div>
      <div><input type="text" class="inputBox"></div>

      <div></div>
      <div>
        <input type="radio" id="valid" name="validstatus" value="valid">
        <label for="valid">Valid</label>
        <input type="radio" id="invalid" name="validstatus" value="invalid">
        <label for="invalid">Invalid</label>
      </div>

      <div></div>
      <div class="bigButton" style="height: 30px">Add Adjustment</div>
    </div>
</div>