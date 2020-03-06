var numOfLanes = 24;
var subDepartments = ["Bowling", "Birthdays", "Pro Shop", "Tournament Lockers"];
var packages = ["Nachos Pins Pepsi", "Pizza Pins Pepsi"];
var products = ["Game Bowling", "Ladies Night", "Food Fight", "2hr fun w/shoes Adult"];
var modifiers = [
                ["League/24/7 $4.00", "Senior/Kids/24/7 $4.00"],
                ["Ladies Night Mod1 $5.00", "Ladies Night Mod2 $4.50"],
                ["Food Fight Mod1 $3.30", "Food Fight Mod2 $3.75"],
                ["2hr fun Mod1 $5.15", "2hr fun Mod2 $4.99"]
                ];
var modifierPrices = [
                    [4.00, 4.00],
                    [5.00, 4.50],
                    [3.30, 3.75],
                    [5.15, 4.99]
                    ];
var sales = [];

var selectedLane = 0;
var selectedSale = -1;
var minSelect = 0;
var maxSelect = 0;
var laneIsSelected = false;
var numOfSales = 0;

function start() {  
    displayLanes();
    displaySubDepartments();
    displayPackages();
    displayProducts();

    $(".laneBtn").click(function() {
        selectLanes(this);
    });

    $("#clearBtn").click(function() {
        toggleLaneButtons(false);
        laneIsSelected = false;
        minSelect = 0;
        maxSelect = 0;
        selectedLane = 0;
    });

    $("#laneSaleBtn").click(function() {
        selectSale(this);
    });

    $("#prodDiv .normalBtn").click(function() {
        displayModifiers(this);
    });

    

    
}

function addSale(selectedProdID, clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedModID = parseInt(matches[0]);
    var selectedProdName = products[selectedProdID];
    var selectedModName = modifiers[selectedProdID][selectedModID];
    var selectedModPrice = modifierPrices[selectedProdID][selectedModID];
    var quantity = 0;

    $("#receipt").append(
        "<div class=\"receipt_sale\" id=\"receipt_sale" + numOfSales +"\">" +
        "<div class=\"row1\"><b>" + products[selectedProdID] + "</b></div>" +
        "<div class=\"row2\">[" + minSelect + "-" + maxSelect + "]</div>" +
        "<div class=\"row3\">" +
        "<div>" + selectedModName + "</div>" +
        "<div><b>$" + modifierPrices[selectedProdID][selectedModID] + "</b></div>" +
        "</div>" +
        "</div>"
    );

    sales.push(new Array());
    sales[numOfSales][0] = quantity;
    sales[numOfSales][1] = minSelect;
    sales[numOfSales][2] = maxSelect;
    sales[numOfSales][3] = selectedProdName;
    sales[numOfSales][4] = selectedModName;
    sales[numOfSales][0] = selectedModPrice;
    
    numOfSales++;

    $("#receipt .receipt_sale").click(function() {
        selectSale(this);
    });
    
}
function selectSale(clickedButton) {
    clearReceiptSelections();
    const s = "#" + $(clickedButton).attr('id');
    const selectedSaleBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedSaleID = parseInt(matches[0]);
    selectedSaleBtn.classList = "receipt_sale_selected";
}
function clearReceiptSelections()
{
    var i;
    for (i = 0; i < sales.length; i++)
    {
        var s = "#receipt_sale" + i;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "receipt_sale";
    }
    selectedSaleID = -1;
}
function selectLanes(clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var number = parseInt(matches[0]);
    if (!laneIsSelected)
    {
        minSelect = number;
        maxSelect = number;
        //toggle lane buttons as off
        toggleLaneButtons(false);
        selectedLane = number;
        laneIsSelected = true;
        //toggle lane button as on
        selectedBtn.classList = "laneBtnBlue";
    }
    else {
        if (number < selectedLane)
        {
            minSelect = number;
            maxSelect = selectedLane;
        }
        else if (number > selectedLane)
        {
            minSelect = selectedLane;
            maxSelect = number;
        }
        else
        {
            minSelect = number;
            maxSelect = number;
        }
        //toggle lane buttons as on
        toggleLaneButtons(true);
        laneIsSelected = false;
    }
    console.log("Selected: " + minSelect + " " + maxSelect);
}
function toggleLaneButtons(isLaneSelected) {
    var laneID;
    if (isLaneSelected)
    {
        for (laneID = minSelect; laneID <= maxSelect; laneID++)
        {
            var s = "#lane" + laneID;
            var selectedBtn = document.querySelector(s);
            selectedBtn.classList = "laneBtnBlue";
        }
    }
    else
    {
        for (laneID = 1; laneID <= numOfLanes; laneID++)
        {
            var s = "#lane" + laneID;
            var selectedBtn = document.querySelector(s);
            selectedBtn.classList = "laneBtn";
        }
    }
}
function displayLanes() {
    var laneID;
    for (laneID = 1; laneID <= numOfLanes; laneID++)
    {
        $("#laneDiv").append("<div class=\"laneBtn\" id=\"lane"+ laneID +"\">Lane " + laneID + "</div>");
    }
}
function displaySubDepartments() {
    var subdptID;
    for (subdptID = 0; subdptID < subDepartments.length; subdptID++)
    {
        $("#subdptDiv").append("<div class=\"greenBtn\" id=\"subdpt" + subdptID + "\">" + subDepartments[subdptID] + "</div>");
    }
}
function displayPackages() {
    var pkgID;
    for (pkgID = 0; pkgID < packages.length; pkgID++)
    {
        $("#pkgDiv").append("<div class=\"yellowBtn\" id=\"pkg" + pkgID + "\">" + packages[pkgID] + "</div>");
    }
}
function displayProducts() {
    var prodID;
    for (prodID = 0; prodID < products.length; prodID++)
    {
        $("#prodDiv").append("<div class=\"normalBtn\" id=\"prod" + prodID + "\">" + products[prodID] + "</div>");
    }
}
function displayModifiers(clickedButton) {
    $("#modDiv").html("");
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedProdID = parseInt(matches[0]);
    var modID;
    for (modID = 0; modID < modifiers[selectedProdID].length; modID++)
    {
        $("#modDiv").append("<div class=\"purpleBtn\" id=\"mod" + modID + "\">" + modifiers[selectedProdID][modID] + "</div>");
    }

    $("#modDiv .purpleBtn").click(function() {
        if (minSelect != 0 && maxSelect != 0)
        {
            addSale(selectedProdID, this);
        }
    });
}

$(document).ready(start);