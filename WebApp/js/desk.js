var numOfLanes = 32;
var maxNumOfLanesPerPage = 24;
var maxNumOfProductsPerPage = 6;
var currentLanePage = 0;
var currentSubDptPage = 0;
var currentPackagePage = 0;
var currentProductsPage = 0;
var currentModifiersPage = 0;
var subDepartments = ["Bowling", "Birthdays", "Pro Shop", "Tournament Lockers", "Test 5", "Test 6", "Test 7", "Test 8", "Test 9", "Test 10", "Test 11"];
var packages = [
                ["Nachos Pins Pepsi", "Pizza Pins Pepsi", "Test1", "Test2", "Test3", "Test4", "Test5"],
                ["Package 2.1", "Package 2.2", "Package 2.3", "Package 2.4", "Package 2.5", "Package 2.6", "Package 2.7", "Package 2.8"],
                ["Package 3.1", "Package 3.2", "Package 3.3", "Package 3.4", "Package 3.5", "Package 3.6", "Package 3.7", "Package 3.8"],
                ["Package 4.1", "Package 4.2", "Package 4.3", "Package 4.4", "Package 4.5", "Package 4.6", "Package 4.7", "Package 4.8"]
               ];
var products = [
                ["Game Bowling", "Ladies Night", "Food Fight", "2hr fun w/shoes Adult", "Test1", "Test2", "Test3", "Test4", "Test5"],
                ["Birthday Prod1", "Birthday Prod2", "Birthday Prod3", "Test1", "Test2", "Test3", "Test4", "Test5"],
                ["Pro Shop Prod1", "Pro Shop Prod2", "Pro Shop Prod3", "Pro Shop Prod4"],
                ["T. Locker 1", "T. Locker 2", "T. Locker 3"]
               ];
var modifiers = [
                    [
                        ["League/24/7 $4.00", "Senior/Kids/24/7 $4.00", "Test 1", "Test 2", "Test 3", "Test 4", "Test 5"],
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
var selectedSubID = -1;
var selectedProdID = -1;
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
        if (selectedSale != -1)
        {
            sales[selectedSale][1] = 0;
            sales[selectedSale][2] = 0;
            updateSale();
        }
    });
    $("#erase").click(function(){
        if (selectedSale != -1)
        {
            sales[selectedSale][0] = 0;
            sales[selectedSale][5] = 0;
            updateSale();
            updateTotal();
        }
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
        scrollSubDepartment(1);
    });
    $("#subdepartments_up_arrow").click(function() {
        scrollSubDepartment(0);
    });
    $("#packages_down_arrow").click(function() {
        scrollPackages(1);
    });
    $("#packages_up_arrow").click(function() {
        scrollPackages(0);
    });
    $("#products_down_arrow").click(function() {
        scrollProducts(1);
    });
    $("#products_up_arrow").click(function() {
        scrollProducts(0);
    });
    $("#modifiers_down_arrow").click(function() {
        scrollModifiers(1);
    });
    $("#modifiers_up_arrow").click(function() {
        scrollModifiers(0);
    });
    $("#lanes_down_arrow").click(function() {
        scrollLanes(1);
    });
    $("#lanes_up_arrow").click(function() {
        scrollLanes(0);
    });
}

function addSale(subID, selectedProdID, clickedButton) {
    clearReceiptSelections();
    selectedSale = numOfSales;
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
        "<div class=\"receipt_sale_selected\" id=\"receipt_sale" + numOfSales +"\" onclick=\"selectSale(" + numOfSales + ")\">" +
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
    
    updateSale();
    updateTotal();
}
function changeLaneForSale()
{
    sales[selectedSale][1] = minSelect;
    sales[selectedSale][2] = maxSelect;
    updateSale();
}
function clearReceiptSelections() {
    var i;
    for (i = 0; i < sales.length; i++)
    {
        var s = "#receipt_sale" + i;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "receipt_sale";
    }
    selectedSale = -1;
}
function clearSubDepartmentBtns() {
    var subdptID;
    for (subdptID = 0; subdptID < maxNumOfProductsPerPage; subdptID++)
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
function displayErrorMsg(msg) {
    $("#errorMsgContainer").html("");
    $("#errorMsgContainer").append("<div class=\"errorMsg\"><i class=\"fa fa-exclamation-circle\"></i>&nbsp;" + msg + "</div>");
}
function displayLanes() {
    var laneID = 1;

    var numberOfPages = Math.floor(numOfLanes/maxNumOfLanesPerPage);
    if (numOfLanes == maxNumOfLanesPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var s = "#lane_page" + i;
        $("#laneDiv").append("<div class=\"lane_page\" id=\""+ s + "\"></div>");
        for (laneID; laneID < i * maxNumOfLanesPerPage + maxNumOfLanesPerPage + 1; laneID++)
        {
            if (laneID <= numOfLanes)
            {
                $(document.getElementById(s)).append("<div class=\"laneBtn\" id=\"lane"+ laneID +"\">Lane " + laneID + "</div>");
            }
            else
            {
                laneID = i * maxNumOfLanesPerPage + maxNumOfLanesPerPage + 1;
            }
        }
        
    }
    document.getElementById("#lane_page0").classList = "lane_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel_enabled";
    }
}
function displaySubDepartments() {
    var subdptID = 0;
    var numOfSubDpts = subDepartments.length;
    var numberOfPages = Math.floor(numOfSubDpts/maxNumOfProductsPerPage);
    if (numOfSubDpts == maxNumOfProductsPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var s = "#sub_page" + i;
        $("#subdptDiv").append("<div class=\"sub_page\" id=\""+ s + "\"></div>");
        for (subdptID; subdptID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; subdptID++)
        {
            if (subDepartments[subdptID] != null)
            {
                $(document.getElementById(s)).append("<div class=\"greenBtn\" id=\"subdpt" + subdptID + "\">" + subDepartments[subdptID] + "</div>");
            }
            else
            {
                subdptID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#sub_page0").classList = "sub_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel_enabled";
    }
    $("#subdptDiv .greenBtn").click(function() {
        displayProducts(this);
        displayPackages(this);
    });
}
function displayPackages(clickedButton) {
    var pkgID = 0;
    document.querySelector("#packages_up_arrow").classList = "arrow_panel";
    document.querySelector("#packages_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    currentPackagePage = 0;
    selectedSubID = parseInt(matches[0]);
    var numOfPackages = packages[selectedSubID].length;
    var numberOfPages = Math.floor(numOfPackages/maxNumOfProductsPerPage);
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#pkg_page" + i;
        $("#pkgDiv").append("<div class=\"pkg_page\" id=\""+ x + "\"></div>");
        for (pkgID; pkgID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; pkgID++)
        {
            if (packages[selectedSubID][pkgID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"yellowBtn\" id=\"pkg" + pkgID + "\">" + packages[selectedSubID][pkgID] + "</div>");
            }
            else
            {
                pkgID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#pkg_page0").classList = "pkg_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel_enabled";
    }
}
function displayProducts(clickedButton) {
    clearSubDepartmentBtns();
    clickedButton.classList = "greenBtnSelected";
    $("#pkgDiv").html("");
    $("#prodDiv").html("");
    $("#modDiv").html("");
    document.querySelector("#products_up_arrow").classList = "arrow_panel";
    document.querySelector("#products_down_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    currentProductsPage = 0;
    selectedSubID = parseInt(matches[0]);
    var prodID = 0;
    var numOfProducts = products[selectedSubID].length;
    var numberOfPages = Math.floor(numOfProducts/maxNumOfProductsPerPage);
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#prod_page" + i;
        $("#prodDiv").append("<div class=\"prod_page\" id=\""+ x + "\"></div>");
        for (prodID; prodID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; prodID++)
        {
            if (products[selectedSubID][prodID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"normalBtn\" id=\"prod" + prodID + "\">" + products[selectedSubID][prodID] + "</div>");
            }
            else
            {
                prodID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#prod_page0").classList = "prod_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel_enabled";
    }

    $("#prodDiv .normalBtn").click(function() {
        displayModifiers(this, selectedSubID);
    });
}
function displayModifiers(clickedButton, subID) {
    clearProdBtns(subID);
    clickedButton.classList = "normalBtnSelected";
    $("#modDiv").html("");
    document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    currentModifiersPage = 0;
    selectedProdID = parseInt(matches[0]);
    var modID = 0;
    var numOfModifiers = modifiers[selectedSubID][selectedProdID].length;
    var numberOfPages = Math.floor(numOfModifiers/maxNumOfProductsPerPage);
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#mod_page" + i;
        $("#modDiv").append("<div class=\"mod_page\" id=\""+ x + "\"></div>");
        for (modID; modID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; modID++)
        {
            if (modifiers[selectedSubID][selectedProdID][modID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"purpleBtn\" id=\"mod" + modID + "\">" + modifiers[subID][selectedProdID][modID] + "</div>");
            }
            else
            {
                modID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#mod_page0").classList = "mod_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel_enabled";
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
    if (min == 0 && max == 0)
    {
        return "-";
    }
    else
    {
        if (min == max)
            return min;
        else
            return min + "-" + max;
    }
}
function scrollLanes(direction)
{
    var numberOfPages = Math.floor(numOfLanes/maxNumOfLanesPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#lane_page" + currentLanePage).classList = "lane_page";
        document.getElementById("#lane_page" + (currentLanePage + 1)).classList = "lane_page_active";
        currentLanePage++;
    }
    else
    {
        document.getElementById("#lane_page" + (currentLanePage - 1)).classList = "lane_page_active";
        document.getElementById("#lane_page" + currentLanePage).classList = "lane_page";
        currentLanePage--;
    }
    if (currentLanePage < numberOfPages)
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel";
    }
    if (currentLanePage > 0)
    {
        document.querySelector("#lanes_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#lanes_up_arrow").classList = "arrow_panel";
    }
}
function scrollSubDepartment(direction)
{
    var numOfSubDpts = subDepartments.length;
    var numberOfPages = Math.floor(numOfSubDpts/maxNumOfProductsPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#sub_page" + currentSubDptPage).classList = "sub_page";
        document.getElementById("#sub_page" + (currentSubDptPage + 1)).classList = "sub_page_active";
        currentSubDptPage++;
    }
    else
    {
        document.getElementById("#sub_page" + (currentSubDptPage - 1)).classList = "sub_page_active";
        document.getElementById("#sub_page" + currentSubDptPage).classList = "sub_page";
        currentSubDptPage--;
    }
    if (currentSubDptPage < numberOfPages)
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel";
    }
    if (currentSubDptPage > 0)
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel";
    }
}
function scrollPackages(direction)
{
    var numOfPackages = packages[selectedSubID].length;
    var numberOfPages = Math.floor(numOfPackages/maxNumOfProductsPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#pkg_page" + currentPackagePage).classList = "pkg_page";
        document.getElementById("#pkg_page" + (currentPackagePage + 1)).classList = "pkg_page_active";
        currentPackagePage++;
    }
    else
    {
        document.getElementById("#pkg_page" + (currentPackagePage - 1)).classList = "pkg_page_active";
        document.getElementById("#pkg_page" + currentPackagePage).classList = "pkg_page";
        currentPackagePage--;
    }
    if (currentPackagePage < numberOfPages)
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel";
    }
    if (currentPackagePage > 0)
    {
        document.querySelector("#packages_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#packages_up_arrow").classList = "arrow_panel";
    }
}
function scrollProducts(direction)
{
    var numOfProducts = products[selectedSubID].length;
    var numberOfPages = Math.floor(numOfProducts/maxNumOfProductsPerPage);
    if (direction == 1)
    {
        document.getElementById("#prod_page" + currentProductsPage).classList = "prod_page";
        document.getElementById("#prod_page" + (currentProductsPage + 1)).classList = "prod_page_active";
        currentProductsPage++;
    }
    else
    {
        document.getElementById("#prod_page" + (currentProductsPage - 1)).classList = "prod_page_active";
        document.getElementById("#prod_page" + currentProductsPage).classList = "prod_page";
        currentProductsPage--;
    }
    if (currentProductsPage < numberOfPages)
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel";
    }
    if (currentProductsPage > 0)
    {
        document.querySelector("#products_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#products_up_arrow").classList = "arrow_panel";
    }
}
function scrollModifiers(direction)
{
    var numOfModifiers = modifiers[selectedSubID][selectedProdID].length;
    var numberOfPages = Math.floor(numOfModifiers/maxNumOfProductsPerPage);
    if (direction == 1)
    {
        document.getElementById("#mod_page" + currentModifiersPage).classList = "mod_page";
        document.getElementById("#mod_page" + (currentModifiersPage + 1)).classList = "mod_page_active";
        currentModifiersPage++;
    }
    else
    {
        document.getElementById("#mod_page" + (currentModifiersPage - 1)).classList = "mod_page_active";
        document.getElementById("#mod_page" + currentModifiersPage).classList = "mod_page";
        currentModifiersPage--;
    }
    if (currentModifiersPage < numberOfPages)
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    }
    if (currentModifiersPage > 0)
    {
        document.querySelector("#modifiers_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    }
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
        $("#laneNumMax").html("");
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
        toggleLaneButtons(true);
        laneIsSelected = false;
        $("#laneNumMax").html(maxSelect);
    }
    if (selectedSale != -1)
    {
        changeLaneForSale();
    }
    $("#laneNumMin").html(minSelect);
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
    $("#laneNumMin").html("");
    $("#laneNumMax").html("");
}
function updateSale() {
    //refreshes html for selected sale
    const newPrice = sales[selectedSale][5];
    const priceString = newPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    $("#receipt_sale" + selectedSale).html(
        "<div class=\"row1\"><b>" + sales[selectedSale][0] + "&nbsp;" + sales[selectedSale][3] + "</b></div>" +
        "<div class=\"row2\">[" + getLaneFormat(sales[selectedSale][1], sales[selectedSale][2]) + "]</div>" +
        "<div class=\"row3\">" +
        "<div>" + sales[selectedSale][4] + "</div>" +
        "<div class=\"productPrice\"><b>" + priceString + "</b></div>" +
        "</div>"
    );
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

$(document).ready(start);