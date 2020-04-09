var numOfLanes = 24;
var maxNumOfLanesPerPage = 24;
var maxNumOfProductsPerPage = 4;
var currentLanePage = 1;
var currentSubDptPage = 1;
var currentPackagePage = 1;
var currentProductsPage = 1;
var currentModifiersPage = 1;
var subDepartments = ["Bowling", "Birthdays", "Pro Shop", "Tournament Lockers", "Test", "Test 2", "Test 3", "Test 4", "Test 5"];
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
var totalPrice = 0;
var salesTax = 1.09;

function start() {  
    displayLanes();
    displaySubDepartments();
    updateTotal();

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

    

    $(".numBtn").click(function() {
        if (selectedSale != -1)
        {
            addQuantity(this);
        }
    });
    $("#subdepartments_down_arrow").click(function() {
        currentSubDptPage++;
        displaySubDepartments();
    });
    $("#subdepartments_up_arrow").click(function() {
        currentSubDptPage--;
        displaySubDepartments();
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
    var quantity = 0;

    laneIsSelected = false;
    sales.push(new Array());
    sales[numOfSales][0] = quantity;
    sales[numOfSales][1] = minSelect;
    sales[numOfSales][2] = maxSelect;
    sales[numOfSales][3] = selectedProdName;
    sales[numOfSales][4] = selectedModName;
    sales[numOfSales][5] = selectedModPrice * sales[numOfSales][0];
    sales[numOfSales][6] = selectedModPrice;

    const priceString = sales[numOfSales][5].toLocaleString('us-US', { style: 'currency', currency: 'USD'});

    $("#receipt").append(
        "<div class=\"receipt_sale\" id=\"receipt_sale" + numOfSales +"\" onclick=\"selectSale(" + numOfSales + ")\">" +
        "<div class=\"row1\"><b>" + getSaleQuantity(numOfSales) + "&nbsp;" + selectedProdName + "</b></div>" +
        "<div class=\"row2\">[" + getLaneFormat(minSelect,maxSelect) + "]</div>" +
        "<div class=\"row3\">" +
        "<div>" + selectedModName + "</div>" +
        "<div class=\"productPrice\"><b>" + priceString + "</b></div>" +
        "</div>" +
        "</div>"
    );

    updateTotal();

    numOfSales++;
    
}
function addQuantity(clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    var selectedNum = parseInt(matches[0]);
    var newSaleString = sales[selectedSale][0] + "" + selectedNum;
    var newQuantity = parseInt(newSaleString);
    
    sales[selectedSale][0] = newQuantity;
    sales[selectedSale][5] = sales[selectedSale][6] * newQuantity;
    const newPrice = sales[selectedSale][5];
    
    const priceString = newPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    
    $("#receipt_sale" + selectedSale).html(
        "<div class=\"row1\"><b>" + newQuantity + "&nbsp;" + sales[selectedSale][3] + "</b></div>" +
        "<div class=\"row2\">[" + getLaneFormat(sales[selectedSale][1], sales[selectedSale][2]) + "]</div>" +
        "<div class=\"row3\">" +
        "<div>" + sales[selectedSale][4] + "</div>" +
        "<div class=\"productPrice\"><b>" + priceString + "</b></div>" +
        "</div>"
    );
    updateTotal();
}
function selectSale(num) {
    clearReceiptSelections();
    const s = "#receipt_sale" + num;
    const selectedSaleBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedSaleID = num;
    selectedSale = selectedSaleID;
    selectedSaleBtn.classList = "receipt_sale_selected";
}
function updateTotal() {
    var i;
    totalPrice = 0;
    for (i = 0; i < sales.length; i++)
    {
        totalPrice += sales[i][5];
    }
    const s = totalPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    const salesTaxString = salesTax.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    $("#saleTotal").html(s);
    $("#salesTax").html(salesTaxString);
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
    for (subdptID = 0; subdptID < maxNumOfProductsPerPage; subdptID++)
    {
        var s = "#subdpt" + subdptID;
        console.log("Trying to select: " + s);
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
    var numOfSubDpts = subDepartments.length;

    $("#subdptDiv").html("");

    if (currentSubDptPage > 1)
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel";
    }

    if (numOfSubDpts - (currentSubDptPage * maxNumOfProductsPerPage) > 0)
    {
        console.log("If: " + currentSubDptPage * maxNumOfProductsPerPage);
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel_enabled";
        for (subdptID = currentSubDptPage * maxNumOfProductsPerPage - maxNumOfProductsPerPage; subdptID < currentSubDptPage * maxNumOfProductsPerPage; subdptID++)
        {
            $("#subdptDiv").append("<div class=\"greenBtn\" id=\"subdpt" + subdptID + "\">" + subDepartments[subdptID] + "</div>");
        }
    }
    else
    {
        console.log("Else: " + (currentSubDptPage * maxNumOfProductsPerPage - maxNumOfProductsPerPage));
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel";
        for (subdptID = currentSubDptPage * maxNumOfProductsPerPage - maxNumOfProductsPerPage; subdptID < currentSubDptPage * maxNumOfProductsPerPage - maxNumOfProductsPerPage + 1; subdptID++)
        {
            $("#subdptDiv").append("<div class=\"greenBtn\" id=\"subdpt" + subdptID + "\">" + subDepartments[subdptID] + "</div>");
        }
    }
    $("#subdptDiv .greenBtn").click(function() {
        displayProducts(this);
        displayPackages(this);
    });
    
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
function getLaneFormat(min, max) {
    if (min == max)
        return min;
    else
        return min + "-" + max;
}
function displayErrorMsg(msg) {
    $("#errorMsgContainer").html("");
    $("#errorMsgContainer").append("<div class=\"errorMsg\"><i class=\"fa fa-exclamation-circle\"></i>&nbsp;" + msg + "</div>");
}

$(document).ready(start);