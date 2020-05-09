<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div class="officePanelGrid" style="grid-template-columns: 100%;">
    <div class="officePanelGridLabel" style="text-align: left; margin-left: 0px;"><b>Purge all Sales Detail records on or before the selected date</b></div>
</div>

<!-- Print out dates that are selected -->
<div class="officePanelGrid" style="grid-template-columns: 100%; margin: 5px 0px 10px 0px;">
    <div class="officePanelGridLabel" style="text-align: left; margin-left: 0px;" id="datesSelected">Transactions have been selected between dates ____ and ____</div>
</div>

<div class="officePanelGrid" style="grid-template-columns: 75px 1fr">
  <div class="officePanelGridLabel">Start Date</div>
  <div>
    <input type="text" class="inputBox" placeholder="mm/dd/yyyy">
  </div>

  <div class="officePanelGridLabel">End Date</div>
  <div>
    <input type="text" class="inputBox" placeholder="mm/dd/yyyy">
  </div>

  <div></div>
  <div>
    <button type="submit" class="saveBtn" style="margin-left: 5px;"><i class="fa fa-check-circle"></i></button>
  </div>
</div>

<div class="separator">
    <div class="topline" style="width: 100%"></div>
    <div class="bottomline" style="width: 100%"></div>
</div>

<div class="officePanelGrid" style="grid-template-columns: 100%;  margin: 5px 0px 10px 0px;">
    <div class="officePanelGridLabel" style="text-align: left; margin-left: 0px;"><b>Purge all Sales Archive records before the selected date</b></div>
</div>

<div class="officePanelGrid" style="grid-template-columns: 75px 1fr;">
    <div class="officePanelGridLabel">Enter Date</div>
    <div>
      <input type="text" class="inputBox" placeholder="mm/dd/yyyy">
    </div>

    <div></div>
    <div>
        <button type="submit" class="saveBtn" style="margin-left: 5px;"><i class="fa fa-check-circle"></i></button>
    </div>
  </div>