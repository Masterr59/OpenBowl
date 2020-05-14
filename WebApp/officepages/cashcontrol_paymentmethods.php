<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
        <div class="officePanelGridLabel" style="margin-top: 0px">
            <select id="paymentmethods" class="dropdownBox">
                <option value="test1">test paymentmethod 1</option>
                <option value="test2">test paymentmethod 2</option>
                <option value="test3">test paymentmethod 3</option>
            </select>
        </div>
        <div style="display: flex;">
            <div class="saveBtn" id="new"><i class="fa fa-file"></i></div>
            <div class="saveBtn" id="edit"><i class="fa fa-save"></i></div>
            <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
            <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
        </div>
        <div class="officePanelGridLabel">Payment Method Description</div>
        <div><input type="text" class="inputBox"></div>
        
        <div></div>
        <div>
            <input type="radio" id="cash" name="paymentmethod" value="cash">
            <label for="cash">Cash</label>
            <input type="radio" id="check" name="paymentmethod" value="check">
            <label for="check">Check</label>
            <input type="radio" id="giftcertificate" name="paymentmethod" value="giftcertificate">
            <label for="giftcertificate">Gift Certificate</label>
            <input type="radio" id="other" name="paymentmethod" value="other">
            <label for="other">Other</label>
        </div>

        <div></div>
        <div>
            <input type="checkbox" id="credentialsrequired" name="credentialsrequired" value="credentialsrequired">
            <label for="credentialsrequired">Credentials required</label><br/>
            <input type="checkbox" id="allowovertender" name="allowovertender" value="allowovertender">
            <label for="allowovertender">Allow over tender</label>
            <input type="text" class="inputBox" placeholder="$0.00" style="width: 75px"><br/>
            <input type="checkbox" id="makedefaultmethod" name="makedefaultmethod" value="makedefaultmethod">
            <label for="makedefaultmethod">Make this the default payment method</label>
        </div>
    </div>
</div>