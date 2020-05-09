<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<div>
    <form method="post" action="" accept-charset="UTF-8" id="officeform">
        <div class="officePanelGrid" style="grid-template-columns: 150px 1fr">
            
            <div class="officePanelGridLabel" style="margin-top: 0px" id="dropdownHandler">

                <?php
                    require_once('./dbconn.php');
                    $sth = $dbconn->prepare("SELECT * FROM department");
                    $sth->execute();

                    echo "<select name='departments' class='dropdownBox' id='departmentDropDown'>";
                    echo '<option value="" id="default">-Select Department-</option>';
                    if($sth->rowCount()):
                    while($row = $sth->fetch(PDO::FETCH_ASSOC)){
                        $cur = "dpt" . $row['depart_id'];
                        $curName = $row['depart_name'];?>
                        
                        <?php echo '<option value="'.$cur.'" id="'.$cur.'">'.$curName.'</option>'; ?>
                        <?php } ?>
                        <?php endif; ?><?php
                    echo "</select>";
                ?> 

            </div>
            <div style="display: flex;">
                <button type="submit" class="saveBtn" name="new"><i class="fa fa-file"></i></button>
                <button type="submit" class="saveBtn" name="edit" id="edit"><i class="fa fa-save"></i></button>
                <div class="saveBtn" id="undo"><i class="fa fa-undo"></i></div>
                <div class="saveBtn" id="delete"><i class="fa fa-trash"></i></div>
            </div>
            <div class="officePanelGridLabel">Department Description</div>
            <div><input type="text" class="inputBox" name="dpt_desc" id="dpt_desc"></div>
            <div class="officePanelGridLabel">Department Identifier</div>
            <div><input type="text" class="inputBox" name="dpt_ident" id="dpt_ident"></div>
            <div></div>
            <div>
                <input type="checkbox" id="excludefromsales" name="exclude" value="excludefromsales">
                <label for="excludefromsales">Exclude from Sales</label>
            </div>
            
        </div>
    </form>

    <div class="separator">
        <div class="topline" style="width: 100%"></div>
        <div class="bottomline" style="width: 100%"></div>
    </div>

    <div>
        Sub Departments associated with this Department<br/>
        <b>Table will be here</b>
    </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>

<script>
$(document).ready(function(){
    var dptID = 0;
    $('#officeform').on('submit',function(e){
        $.ajax({
            url:'./submit.php',
            data:$(this).serialize(),
            type:'POST',
            success:function(data){
                var x = document.getElementById("departmentDropDown");
                var option = document.createElement("option");
                option.text = $("#dpt_desc").val();
                x.add(option);
            },
            error:function(data){
                console.log("Error");
            }
        });
        e.preventDefault();
    });

    $("#delete").on('click', function(){
        if (dptID != 0)
        {
            var ajaxRequest = {
                url: './delete.php',
                type: 'POST',
                data: { 'depart_id' : dptID },
                success:function(data){
                    $("#dpt_desc").val("");
                    $("#dpt_ident").val("");
                    $("#excludefromsales").prop("checked", false);
                    
                    var selectedDropDownItem = document.getElementById("departmentDropDown");
                    selectedDropDownItem.remove(selectedDropDownItem.selectedIndex);
                },
                error:function(data){
                    console.log("Error what the fuck");
                }
            }
            $.ajax(ajaxRequest);
        }
    });

    $("#undo").on('click', function(){
        if (dptID != 0)
            loadFields();
    });

    $("#departmentDropDown option").on('click', function(){
        const s = "#" + $(this).attr('id');
        if (s != "#default")
        {
            var matches = s.match(/(\d+)/);
            dptID = parseInt(matches[0]);

            loadFields();
        }

    });

    function loadFields()
    {
        var ajaxRequest = {
            url: './retrieve.php',
            type: 'POST',
            data: { 'depart_id' : dptID },
            dataType: 'json',
            success:function(JSONObject){
                $("#dpt_desc").val(JSONObject[0]["depart_name"]);
                $("#dpt_ident").val(JSONObject[0]["depart_identifier"]);
                if (JSONObject[0]["exclude_from_sales"]==1)
                    $("#excludefromsales").prop("checked", true);
                else
                    $("#excludefromsales").prop("checked", false);
            },
            error:function(JSONObject){
                console.log("Error what the fuck");
            }
        }
        $.ajax(ajaxRequest);
    }
});
</script>