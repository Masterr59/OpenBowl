var numOfLanes = 24;
var subDepartments = ["Bowling", "Birthdays", "Pro Shop", "Tournament Lockers"];
var packages = ["Nachos Pins Pepsi", "Pizza Pins Pepsi"];
var products = [
                ["Game Bowling", "Ladies Night", "Food Fight", "2hr fun w/shoes Adult"],
                ["Birthday Prod1", "Birthday Prod2", "Birthday Prod3"],
                ["Pro Shop Prod1", "Pro Shop Prod2", "Pro Shop Prod3", "Pro Shop Prod4"],
                ["T. Locker 1", "T. Locker 2", "T. Locker 3"]
               ];
var modifiers = [
                    [
                        ["League/24/7 $4.00", "Senior/Kids/24/7 $4.00"],
                        ["Ladies Night Mod1 $5.00", "Ladies Night Mod2 $4.50"],
                        ["Food Fight Mod1 $3.30", "Food Fight Mod2 $3.75"],
                        ["2hr fun Mod1 $5.15", "2hr fun Mod2 $4.99"]
                    ],
                    [
                        ["Birthday Mod 1", "Birthday Mod 2", "Birthday Mod 3"],
                        ["Birthday Mod2 1", "Birthday Mod2 2", "Birthday Mod2 3"],
                        ["Birthday Mod3 1", "Birthday Mod3 2", "Birthday Mod3 3"]
                    ],
                    [
                        ["Pro Shop Mod 1", "Pro Shop Mod 2", "Pro Shop Mod 3"],
                        ["Pro Shop2 Mod 1", "Pro Shop2 Mod 2", "Pro Shop2 Mod 3"],
                        ["Pro Shop3 Mod 1", "Pro Shop3 Mod 2", "Pro Shop3 Mod 3"],
                        ["Pro Shop4 Mod 1", "Pro Shop4 Mod 2", "Pro Shop4 Mod 3"]
                    ],
                    [
                        ["Locker Mod 1", "Locker Mod 2", "Locker Mod 3"],
                        ["Locker Mod2 1", "Locker Mod2 2", "Locker Mod2 3"],
                        ["Locker Mod3 1", "Locker Mod3 2", "Locker Mod3 3"]
                    ]
                ];
var modifierPrices = [
                        [
                            [4.00, 4.00],
                            [5.00, 4.50],
                            [3.30, 3.75],
                            [5.15, 4.99]
                        ],
                        [
                            [4.00, 4.00, 4.05],
                            [5.00, 4.50, 4.25],
                            [3.30, 3.75, 3.00],
                        ],
                        [
                            [4.00, 4.00, 5.65],
                            [5.00, 4.50, 4.23],
                            [3.30, 3.75, 3.25],
                            [5.15, 4.99, 5.05]
                        ],
                        [
                            [4.00, 4.00, 4.05],
                            [5.00, 4.50, 4.25],
                            [3.30, 3.75, 3.00],
                        ]
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

    $("#subdptDiv .greenBtn").click(function() {
        displayProducts(this);
        displayPackages(this);
    });


}

function addSale(subID, selectedProdID, clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedModID = parseInt(matches[0]);
    var selectedProdName = products[subID][selectedProdID];
    var selectedModName = modifiers[subID][selectedProdID][selectedModID];
    var selectedModPrice = modifierPrices[subID][selectedProdID][selectedModID];
    var quantity = 1;

    laneIsSelected = false;
    sales.push(new Array());
    sales[numOfSales][0] = quantity;
    sales[numOfSales][1] = minSelect;
    sales[numOfSales][2] = maxSelect;
    sales[numOfSales][3] = selectedProdName;
    sales[numOfSales][4] = selectedModName;
    sales[numOfSales][5] = selectedModPrice;

    $("#receipt").append(
        "<div class=\"receipt_sale\" id=\"receipt_sale" + numOfSales +"\">" +
        "<div class=\"row1\"><b>" + getSaleQuantity(numOfSales) + "&nbsp;" + selectedProdName + "</b></div>" +
        "<div class=\"row2\">[" + getLaneFormat() + "]</div>" +
        "<div class=\"row3\">" +
        "<div>" + selectedModName + "</div>" +
        "<div><b>$" + selectedModPrice * sales[numOfSales][0] + "</b></div>" +
        "</div>" +
        "</div>"
    );

    numOfSales++;

    $("#receipt .receipt_sale").click(function() {
        selectSale(this);
    });
}
function updateTotal() {

}
function selectSale(clickedButton) {
    clearReceiptSelections();
    const s = "#" + $(clickedButton).attr('id');
    const selectedSaleBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedSaleID = parseInt(matches[0]);
    selectedSaleBtn.classList = "receipt_sale_selected";
}
function clearReceiptSelections() {
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
function clearSubDepartmentBtns() {
    var subdptID;
    for (subdptID = 0; subdptID < subDepartments.length; subdptID++)
    {
        var s = "#subdpt" + subdptID;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "greenBtn";
    }
}
function clearProdBtns(subID) {
    var prodID;
    for (prodID = 0; prodID < products[subID].length; prodID++)
    {
        var s = "#prod" + prodID;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "normalBtn";
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
function displayPackages(clickedButton) {
    var pkgID;
    for (pkgID = 0; pkgID < packages.length; pkgID++)
    {
        $("#pkgDiv").append("<div class=\"yellowBtn\" id=\"pkg" + pkgID + "\">" + packages[pkgID] + "</div>");
    }
}
function displayProducts(clickedButton) {
    clearSubDepartmentBtns();
    clickedButton.classList = "greenBtnSelected";
    $("#pkgDiv").html("");
    $("#prodDiv").html("");
    $("#modDiv").html("");
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedSubID = parseInt(matches[0]);
    var prodID;
    for (prodID = 0; prodID < products[selectedSubID].length; prodID++)
    {
        $("#prodDiv").append("<div class=\"normalBtn\" id=\"prod" + prodID + "\">" + products[selectedSubID][prodID] + "</div>");
    }
    $("#prodDiv .normalBtn").click(function() {
        displayModifiers(this, selectedSubID);
    });
}
function displayModifiers(clickedButton, subID) {
    clearProdBtns(subID);
    clickedButton.classList = "normalBtnSelected";
    $("#modDiv").html("");
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedProdID = parseInt(matches[0]);
    var modID;
    for (modID = 0; modID < modifiers[subID][selectedProdID].length; modID++)
    {
        $("#modDiv").append("<div class=\"purpleBtn\" id=\"mod" + modID + "\">" + modifiers[subID][selectedProdID][modID] + "</div>");
    }

    $("#modDiv .purpleBtn").click(function() {
        if (minSelect != 0 && maxSelect != 0)
        {
            addSale(subID, selectedProdID, this);
        }
        else
        {
            displayErrorMsg("Error: No lane(s) selected!");
        }
    });
}
function getSaleQuantity(saleID) {
    return sales[saleID][0];
}
function getLaneFormat() {
    if (minSelect == maxSelect)
        return minSelect;
    else
        return minSelect + "-" + maxSelect;
}
function displayErrorMsg(msg) {
    $("#errorMsgContainer").html("");
    $("#errorMsgContainer").append("<div class=\"errorMsg\"><i class=\"fa fa-exclamation-circle\"></i>&nbsp;" + msg + "</div>");
}

$(document).ready(start);