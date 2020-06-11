-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 11, 2020 at 09:09 PM
-- Server version: 10.3.16-MariaDB
-- PHP Version: 7.3.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbs284379`
--

-- --------------------------------------------------------

--
-- Table structure for table `adjustment`
--

CREATE TABLE `adjustment` (
  `adjustment_id` int(11) NOT NULL,
  `adjustment_name` varchar(50) NOT NULL,
  `item_adjustment` tinyint(1) NOT NULL,
  `adjustment_percentage` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `adjustment`
--

INSERT INTO `adjustment` (`adjustment_id`, `adjustment_name`, `item_adjustment`, `adjustment_percentage`) VALUES
(1, 'Test Adjustment', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `adjustment_message`
--

CREATE TABLE `adjustment_message` (
  `adjustment_message_id` int(11) NOT NULL,
  `user_selects_cancel` varchar(128) NOT NULL,
  `user_selects_undo` varchar(128) NOT NULL,
  `user_cancels_active` varchar(128) NOT NULL,
  `user_marks_active` varchar(128) NOT NULL,
  `user_cancels_old_tabs` varchar(128) NOT NULL,
  `user_marks_old_tabs` varchar(128) NOT NULL,
  `system_cancels` varchar(128) NOT NULL,
  `system_closes` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `adjustment_message`
--

INSERT INTO `adjustment_message` (`adjustment_message_id`, `user_selects_cancel`, `user_selects_undo`, `user_cancels_active`, `user_marks_active`, `user_cancels_old_tabs`, `user_marks_old_tabs`, `system_cancels`, `system_closes`) VALUES
(1, 'Cancelled at desk', 'Cancelled when undoing separate tabs', 'Active transaction cancelled', 'Active transaction marked paid', 'Old tab cancelled', 'Old tab marked paid', 'Cancelled by System', 'Mark paid by System');

-- --------------------------------------------------------

--
-- Table structure for table `banking`
--

CREATE TABLE `banking` (
  `banking_id` int(11) NOT NULL,
  `banking_name` varchar(50) NOT NULL,
  `sub_depart_id` int(11) NOT NULL,
  `pay_out` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `banking`
--

INSERT INTO `banking` (`banking_id`, `banking_name`, `sub_depart_id`, `pay_out`) VALUES
(3, 'Test Banking', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `cash_control_policies`
--

CREATE TABLE `cash_control_policies` (
  `policy_id` int(11) NOT NULL,
  `day_start_time` time NOT NULL,
  `maximum_tab_amount` int(11) NOT NULL,
  `tab_refresh_time` int(11) NOT NULL,
  `close_tabs_end_of_day` tinyint(1) NOT NULL,
  `days_to_save_history` int(11) NOT NULL,
  `allow_lane_auto_shutdown` tinyint(1) NOT NULL,
  `show_euro_currency_tools` tinyint(1) NOT NULL,
  `euro_currency_conversion_value` int(11) NOT NULL,
  `close_shifts_end_of_day` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cash_control_policies`
--

INSERT INTO `cash_control_policies` (`policy_id`, `day_start_time`, `maximum_tab_amount`, `tab_refresh_time`, `close_tabs_end_of_day`, `days_to_save_history`, `allow_lane_auto_shutdown`, `show_euro_currency_tools`, `euro_currency_conversion_value`, `close_shifts_end_of_day`) VALUES
(1, '08:00:00', 1, 5, 1, 7, 1, 1, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `center_information`
--

CREATE TABLE `center_information` (
  `center_id` int(11) NOT NULL,
  `center_name` varchar(50) NOT NULL,
  `owner_name` varchar(50) NOT NULL,
  `contact_person` varchar(50) NOT NULL,
  `center_identification_number` int(11) NOT NULL,
  `address` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `state` varchar(25) NOT NULL,
  `postal_code` varchar(25) NOT NULL,
  `phone` varchar(25) NOT NULL,
  `fax` varchar(25) NOT NULL,
  `modem` varchar(25) NOT NULL,
  `email` varchar(75) NOT NULL,
  `number_of_lanes` int(11) NOT NULL,
  `scoring` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `center_information`
--

INSERT INTO `center_information` (`center_id`, `center_name`, `owner_name`, `contact_person`, `center_identification_number`, `address`, `city`, `state`, `postal_code`, `phone`, `fax`, `modem`, `email`, `number_of_lanes`, `scoring`) VALUES
(1, 'Test Bowling Alley', 'Eric Barrio', 'Eric Barrio', 12345, '8946 W Bowl St.', 'Bowler City', 'WA', '99216', '(509) 123-4567', '', '', 'testbowl@testbowl.com', 32, 1);

-- --------------------------------------------------------

--
-- Table structure for table `customer_receipt`
--

CREATE TABLE `customer_receipt` (
  `customer_receipt_id` int(11) NOT NULL,
  `header_line_1` varchar(50) NOT NULL,
  `header_line_2` varchar(50) NOT NULL,
  `header_line_3` varchar(50) NOT NULL,
  `header_line_4` varchar(50) NOT NULL,
  `header_line_5` varchar(50) NOT NULL,
  `footer_line_1` varchar(50) NOT NULL,
  `footer_line_2` varchar(50) NOT NULL,
  `footer_line_3` varchar(50) NOT NULL,
  `footer_line_4` varchar(50) NOT NULL,
  `footer_line_5` varchar(50) NOT NULL,
  `tax_total` varchar(50) NOT NULL,
  `line_item_total` varchar(50) NOT NULL,
  `paid` varchar(50) NOT NULL,
  `change_label` varchar(50) NOT NULL,
  `due` varchar(50) NOT NULL,
  `payment_total` varchar(50) NOT NULL,
  `charge_total` varchar(50) NOT NULL,
  `tender_total` varchar(50) NOT NULL,
  `show_package_item_prices` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `customer_receipt`
--

INSERT INTO `customer_receipt` (`customer_receipt_id`, `header_line_1`, `header_line_2`, `header_line_3`, `header_line_4`, `header_line_5`, `footer_line_1`, `footer_line_2`, `footer_line_3`, `footer_line_4`, `footer_line_5`, `tax_total`, `line_item_total`, `paid`, `change_label`, `due`, `payment_total`, `charge_total`, `tender_total`, `show_package_item_prices`) VALUES
(1, 'Welcome To', 'NORTH BOWL', '', 'We Appreciate', 'Your Business!', 'Christmas is almost here', 'Schedule Christmas parties Now!', 'Inquire At the Desk', '', '', 'Tax Total', 'Total', 'Paid', 'Change', 'Due', 'Payment Total', 'Charge Total', 'Tender Total', 1);

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE `department` (
  `depart_id` int(11) NOT NULL,
  `depart_name` varchar(75) NOT NULL,
  `created_by` int(11) NOT NULL,
  `depart_identifier` varchar(75) NOT NULL,
  `exclude_from_sales` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `department`
--

INSERT INTO `department` (`depart_id`, `depart_name`, `created_by`, `depart_identifier`, `exclude_from_sales`) VALUES
(3, 'Bowling', 4, '', 1);

-- --------------------------------------------------------

--
-- Table structure for table `device`
--

CREATE TABLE `device` (
  `device_id` int(11) NOT NULL,
  `device_type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `ip_address` varchar(15) NOT NULL,
  `device_config` varchar(128) NOT NULL,
  `device_name` varchar(75) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `device_type`
--

CREATE TABLE `device_type` (
  `device_type_id` int(11) NOT NULL,
  `device_type_name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `game`
--

CREATE TABLE `game` (
  `game_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `frames` varchar(31) NOT NULL,
  `speed` varchar(106) NOT NULL,
  `pins` varchar(132) NOT NULL,
  `score` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lane`
--

CREATE TABLE `lane` (
  `lane_id` int(11) NOT NULL,
  `type` varchar(75) NOT NULL,
  `lane_status_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lane_status`
--

CREATE TABLE `lane_status` (
  `lane_status_id` int(11) NOT NULL,
  `status_description` varchar(75) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `login_history`
--
-- Error reading structure for table dbs284379.login_history: #1932 - Table 'dbs284379.login_history' doesn't exist in engine
-- Error reading data for table dbs284379.login_history: #1064 - You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near 'FROM `dbs284379`.`login_history`' at line 1

-- --------------------------------------------------------

--
-- Table structure for table `modifier`
--

CREATE TABLE `modifier` (
  `modifier_id` int(11) NOT NULL,
  `modifier_name` varchar(50) NOT NULL,
  `product_id` int(11) NOT NULL,
  `modifier_category` varchar(50) NOT NULL,
  `modifier_price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `modifier`
--

INSERT INTO `modifier` (`modifier_id`, `modifier_name`, `product_id`, `modifier_category`, `modifier_price`) VALUES
(1, '24/7', 8, '', 1.1),
(3, 'Bowling 24/7', 12, '', 5),
(4, 'Bowling 6PM', 12, '', 6);

-- --------------------------------------------------------

--
-- Table structure for table `modifier_period`
--

CREATE TABLE `modifier_period` (
  `modifier_period_id` int(11) NOT NULL,
  `modifier_period_name` varchar(75) NOT NULL,
  `monday` tinyint(1) NOT NULL,
  `tuesday` tinyint(1) NOT NULL,
  `wednesday` tinyint(1) NOT NULL,
  `thursday` tinyint(1) NOT NULL,
  `friday` tinyint(1) NOT NULL,
  `saturday` tinyint(1) NOT NULL,
  `sunday` tinyint(1) NOT NULL,
  `starting_time` time NOT NULL,
  `ending_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `modifier_period`
--

INSERT INTO `modifier_period` (`modifier_period_id`, `modifier_period_name`, `monday`, `tuesday`, `wednesday`, `thursday`, `friday`, `saturday`, `sunday`, `starting_time`, `ending_time`) VALUES
(1, '24/7', 1, 1, 1, 1, 1, 1, 1, '00:00:00', '23:59:00');

-- --------------------------------------------------------

--
-- Table structure for table `package`
--

CREATE TABLE `package` (
  `package_id` int(11) NOT NULL,
  `package_name` varchar(50) NOT NULL,
  `contains_lane` tinyint(1) NOT NULL,
  `sub_depart_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `package`
--

INSERT INTO `package` (`package_id`, `package_name`, `contains_lane`, `sub_depart_id`) VALUES
(1, 'Bowling Package', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `package_product`
--

CREATE TABLE `package_product` (
  `package_product_id` int(11) NOT NULL,
  `package_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `package_product`
--

INSERT INTO `package_product` (`package_product_id`, `package_id`, `product_id`, `quantity`) VALUES
(1, 1, 8, 1),
(2, 1, 9, 1);

-- --------------------------------------------------------

--
-- Table structure for table `payment_method`
--

CREATE TABLE `payment_method` (
  `payment_method_id` int(11) NOT NULL,
  `payment_method_name` varchar(50) NOT NULL,
  `payment_type` int(11) NOT NULL,
  `credentials_required` tinyint(1) NOT NULL,
  `allow_over_tender` tinyint(1) NOT NULL,
  `tender_amount` double NOT NULL,
  `default_method` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `payment_method`
--

INSERT INTO `payment_method` (`payment_method_id`, `payment_method_name`, `payment_type`, `credentials_required`, `allow_over_tender`, `tender_amount`, `default_method`) VALUES
(1, 'Test Payment Method 1', 2, 1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `payment_type`
--

CREATE TABLE `payment_type` (
  `payment_type_id` int(11) NOT NULL,
  `payment_type_name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE `player` (
  `player_id` int(11) NOT NULL,
  `player_name` varchar(75) NOT NULL,
  `username` varchar(75) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `policies`
--

CREATE TABLE `policies` (
  `policy_id` int(11) NOT NULL,
  `card_readers` tinyint(1) NOT NULL,
  `reset_password_first_login` tinyint(1) NOT NULL,
  `enforce_password_history` tinyint(1) NOT NULL,
  `enforce_strong_passwords` tinyint(1) NOT NULL,
  `max_days_pass_active` int(11) NOT NULL,
  `max_days_inactivity` int(11) NOT NULL,
  `max_failed_attempts` int(11) NOT NULL,
  `inactive_timeout` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `policies`
--

INSERT INTO `policies` (`policy_id`, `card_readers`, `reset_password_first_login`, `enforce_password_history`, `enforce_strong_passwords`, `max_days_pass_active`, `max_days_inactivity`, `max_failed_attempts`, `inactive_timeout`) VALUES
(1, 0, 0, 0, 0, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` int(11) NOT NULL,
  `sub_depart_id` int(11) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_type_id` int(11) NOT NULL,
  `tax_type_id` int(11) NOT NULL,
  `enable_modifiers` tinyint(1) NOT NULL,
  `stock_keeping_unit` int(11) NOT NULL,
  `cash_register_order` int(11) NOT NULL,
  `out_of_stock` tinyint(1) NOT NULL,
  `price` double NOT NULL,
  `always_open_price` tinyint(1) NOT NULL,
  `sold_by` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `sub_depart_id`, `product_name`, `product_type_id`, `tax_type_id`, `enable_modifiers`, `stock_keeping_unit`, `cash_register_order`, `out_of_stock`, `price`, `always_open_price`, `sold_by`) VALUES
(8, 1, 'Ladies Night', 1, 1, 1, 123, 12, 1, 4.15, 1, 1),
(9, 1, '1 Hour of fun w/ shoes', 1, 1, 0, 1233, 13, 0, 5.05, 1, 0),
(11, 1, 'Food Fight', 1, 1, 0, 1516, 15, 0, 3.15, 1, 0),
(12, 1, 'Game Bowling', 1, 1, 1, 1517, 15, 0, 4.99, 1, 0),
(13, 1, 'Kids Bowl Free', 1, 1, 0, 1518, 16, 0, 0, 1, 0),
(14, 1, 'Hours of Fun', 1, 1, 0, 1519, 17, 0, 5.5, 1, 0),
(15, 1, 'Party Time', 1, 1, 0, 1520, 18, 0, 7.35, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `product_type`
--

CREATE TABLE `product_type` (
  `product_type_id` int(11) NOT NULL,
  `product_type_name` varchar(75) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `product_type`
--

INSERT INTO `product_type` (`product_type_id`, `product_type_name`) VALUES
(1, 'Standard'),
(2, 'Game Bowling'),
(3, 'Time Bowling');

-- --------------------------------------------------------

--
-- Table structure for table `product_useage`
--

CREATE TABLE `product_useage` (
  `product_useage_id` int(11) NOT NULL,
  `transaction_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `product_qty` int(11) NOT NULL,
  `lane_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `shift`
--

CREATE TABLE `shift` (
  `shift_id` int(11) NOT NULL,
  `begin_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `user_start` int(11) NOT NULL,
  `user_end` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sub_department`
--

CREATE TABLE `sub_department` (
  `sub_depart_id` int(11) NOT NULL,
  `depart_id` int(11) NOT NULL,
  `sub_depart_name` varchar(75) NOT NULL,
  `created_by` int(11) NOT NULL,
  `sub_depart_identifier` varchar(75) NOT NULL,
  `exclude_from_sales` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sub_department`
--

INSERT INTO `sub_department` (`sub_depart_id`, `depart_id`, `sub_depart_name`, `created_by`, `sub_depart_identifier`, `exclude_from_sales`) VALUES
(1, 3, 'Bowling', 4, '', 1),
(2, 3, 'Tournament Lockers', 4, '', 1),
(3, 3, 'Birthdays and Groups', 4, '', 0),
(4, 3, 'Pro Shop', 4, '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `tax_type`
--

CREATE TABLE `tax_type` (
  `tax_type_id` int(11) NOT NULL,
  `tax_type_name` varchar(30) NOT NULL,
  `tax_rate` double NOT NULL,
  `sub_depart_id` int(11) NOT NULL,
  `transaction_tax` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tax_type`
--

INSERT INTO `tax_type` (`tax_type_id`, `tax_type_name`, `tax_rate`, `sub_depart_id`, `transaction_tax`) VALUES
(1, 'Bowling Sales Tax', 10, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `terminal`
--

CREATE TABLE `terminal` (
  `terminal_id` int(11) NOT NULL,
  `terminal_description` varchar(75) NOT NULL,
  `computer_name` varchar(75) NOT NULL,
  `ip_address` varchar(32) NOT NULL,
  `security_device` int(11) NOT NULL,
  `pos_session_logoff` tinyint(1) NOT NULL,
  `end_shift_rules` tinyint(1) NOT NULL,
  `apply_desk_timeout` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `terminal`
--

INSERT INTO `terminal` (`terminal_id`, `terminal_description`, `computer_name`, `ip_address`, `security_device`, `pos_session_logoff`, `end_shift_rules`, `apply_desk_timeout`) VALUES
(1, 'Test Terminal 1', 'Tester', '192.168.1.1', 0, 1, 0, 0),
(2, 'Test Terminal 2', 'Tester 2', '192.168.1.2', 0, 1, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transaction_id` int(11) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `transaction_status_id` int(11) NOT NULL,
  `device_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `shift_id` int(11) NOT NULL,
  `payment_type_id` int(11) NOT NULL,
  `price_total` double NOT NULL,
  `lane_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `user_role_id` int(11) NOT NULL,
  `username` varchar(75) NOT NULL,
  `user_salt` char(64) NOT NULL,
  `user_hash` char(255) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `last_login` datetime NOT NULL,
  `full_user_name` varchar(75) NOT NULL,
  `waitstaff_code` varchar(10) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address1` varchar(50) NOT NULL,
  `address2` varchar(50) NOT NULL,
  `address3` varchar(50) NOT NULL,
  `address4` varchar(50) NOT NULL,
  `inactive_user` tinyint(1) NOT NULL,
  `reset_password_next_login` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `user_role_id`, `username`, `user_salt`, `user_hash`, `active`, `last_login`, `full_user_name`, `waitstaff_code`, `email`, `phone`, `address1`, `address2`, `address3`, `address4`, `inactive_user`, `reset_password_next_login`) VALUES
(4, 1, 'Eric', 'ofiajweof', 'oiawjefoi', 0, '2020-05-08 00:00:00', 'Eric Barrio', 'EB', 'ebarrio19@eagles.ewu.edu', '509-981-7309', '9313 W 72 Avenue', 'Cheney, WA 99004', '', '', 0, 0),
(5, 1, 'Marcus', '', '', 0, '0000-00-00 00:00:00', 'Marcus Goss', 'MG', 'Zimandgrim@yahoo.com', '123-456-7891', '123 ABC Street', '', '', '', 0, 0),
(6, 1, 'Patrick', '', '', 0, '0000-00-00 00:00:00', 'Patrick Enburg', 'PE', 'patrick@enburg.info', '123-456-7891', '456 DEF Street', '', '', '', 1, 0),
(7, 1, 'Brian', '', '', 0, '0000-00-00 00:00:00', 'Brian Bos', 'BB', 'bbos@eagles.ewu.edu', '509-939-4083', '789 GHI Street', '', '', '', 1, 1),
(8, 4, 'Tom', '', '', 0, '0000-00-00 00:00:00', 'Tom', '1', 'aasf', '123', '123', '', '', '', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `user_role`
--

CREATE TABLE `user_role` (
  `user_role_id` int(11) NOT NULL,
  `user_role_name` varchar(75) NOT NULL,
  `add_user` tinyint(1) NOT NULL,
  `remove_user` tinyint(1) NOT NULL,
  `rename_user` tinyint(1) NOT NULL,
  `add_transaction` tinyint(1) NOT NULL,
  `delete_transaction` tinyint(1) NOT NULL,
  `generate_reports` tinyint(1) NOT NULL,
  `scorer_control` tinyint(1) NOT NULL,
  `manage_scorers` tinyint(1) NOT NULL,
  `display_game` tinyint(1) NOT NULL,
  `manage_displays` tinyint(1) NOT NULL,
  `allow_caption_editing` tinyint(1) NOT NULL,
  `prevent_tab_gratuity_viewing` tinyint(1) NOT NULL,
  `can_type_name_password` tinyint(1) NOT NULL,
  `can_swipe_magnetic_card` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user_role`
--

INSERT INTO `user_role` (`user_role_id`, `user_role_name`, `add_user`, `remove_user`, `rename_user`, `add_transaction`, `delete_transaction`, `generate_reports`, `scorer_control`, `manage_scorers`, `display_game`, `manage_displays`, `allow_caption_editing`, `prevent_tab_gratuity_viewing`, `can_type_name_password`, `can_swipe_magnetic_card`) VALUES
(1, 'Developer', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0),
(3, 'Administrator', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
(4, 'Staff', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `adjustment`
--
ALTER TABLE `adjustment`
  ADD PRIMARY KEY (`adjustment_id`);

--
-- Indexes for table `adjustment_message`
--
ALTER TABLE `adjustment_message`
  ADD PRIMARY KEY (`adjustment_message_id`);

--
-- Indexes for table `banking`
--
ALTER TABLE `banking`
  ADD PRIMARY KEY (`banking_id`),
  ADD KEY `FK_Banking_SubDepartment` (`sub_depart_id`);

--
-- Indexes for table `cash_control_policies`
--
ALTER TABLE `cash_control_policies`
  ADD PRIMARY KEY (`policy_id`);

--
-- Indexes for table `center_information`
--
ALTER TABLE `center_information`
  ADD PRIMARY KEY (`center_id`);

--
-- Indexes for table `customer_receipt`
--
ALTER TABLE `customer_receipt`
  ADD PRIMARY KEY (`customer_receipt_id`);

--
-- Indexes for table `department`
--
ALTER TABLE `department`
  ADD PRIMARY KEY (`depart_id`),
  ADD KEY `FK_User_Department` (`created_by`);

--
-- Indexes for table `device`
--
ALTER TABLE `device`
  ADD PRIMARY KEY (`device_id`),
  ADD KEY `FK_DeviceType_Device` (`device_type_id`);

--
-- Indexes for table `device_type`
--
ALTER TABLE `device_type`
  ADD PRIMARY KEY (`device_type_id`);

--
-- Indexes for table `game`
--
ALTER TABLE `game`
  ADD PRIMARY KEY (`game_id`),
  ADD KEY `FK_PlayerID_Game` (`player_id`);

--
-- Indexes for table `lane`
--
ALTER TABLE `lane`
  ADD PRIMARY KEY (`lane_id`),
  ADD KEY `FK_Lane_LaneStatus` (`lane_status_id`);

--
-- Indexes for table `lane_status`
--
ALTER TABLE `lane_status`
  ADD PRIMARY KEY (`lane_status_id`);

--
-- Indexes for table `modifier`
--
ALTER TABLE `modifier`
  ADD PRIMARY KEY (`modifier_id`),
  ADD KEY `FK_Modifier_Product` (`product_id`);

--
-- Indexes for table `modifier_period`
--
ALTER TABLE `modifier_period`
  ADD PRIMARY KEY (`modifier_period_id`);

--
-- Indexes for table `package`
--
ALTER TABLE `package`
  ADD PRIMARY KEY (`package_id`),
  ADD KEY `FK_Package_SubDepartment` (`sub_depart_id`);

--
-- Indexes for table `package_product`
--
ALTER TABLE `package_product`
  ADD PRIMARY KEY (`package_product_id`),
  ADD KEY `FK_PP_Product` (`product_id`),
  ADD KEY `FK_PP_Package` (`package_id`);

--
-- Indexes for table `payment_method`
--
ALTER TABLE `payment_method`
  ADD PRIMARY KEY (`payment_method_id`);

--
-- Indexes for table `payment_type`
--
ALTER TABLE `payment_type`
  ADD PRIMARY KEY (`payment_type_id`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`player_id`);

--
-- Indexes for table `policies`
--
ALTER TABLE `policies`
  ADD PRIMARY KEY (`policy_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `FK_SubDepartment_Product` (`sub_depart_id`),
  ADD KEY `FK_ProductType_Product` (`product_type_id`),
  ADD KEY `FK_TaxType_Product` (`tax_type_id`);

--
-- Indexes for table `product_type`
--
ALTER TABLE `product_type`
  ADD PRIMARY KEY (`product_type_id`);

--
-- Indexes for table `product_useage`
--
ALTER TABLE `product_useage`
  ADD PRIMARY KEY (`product_useage_id`),
  ADD KEY `FK_Product_ProductUseage` (`product_id`),
  ADD KEY `FK_Transaction_ProductUseage` (`transaction_id`),
  ADD KEY `FK_Lane_ProductUseage` (`lane_id`);

--
-- Indexes for table `shift`
--
ALTER TABLE `shift`
  ADD PRIMARY KEY (`shift_id`),
  ADD KEY `FK_User_ShiftUserStart` (`user_start`),
  ADD KEY `FK_User_ShiftUserEnd` (`user_end`);

--
-- Indexes for table `sub_department`
--
ALTER TABLE `sub_department`
  ADD PRIMARY KEY (`sub_depart_id`),
  ADD KEY `FK_DepartmentSubDepartment` (`depart_id`),
  ADD KEY `FK_User_SubDepartment` (`created_by`);

--
-- Indexes for table `tax_type`
--
ALTER TABLE `tax_type`
  ADD PRIMARY KEY (`tax_type_id`),
  ADD KEY `FK_TaxType_SubDepartment` (`sub_depart_id`);

--
-- Indexes for table `terminal`
--
ALTER TABLE `terminal`
  ADD PRIMARY KEY (`terminal_id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `FK_Lane_Transaction` (`lane_id`),
  ADD KEY `FK_PaymentType_Transaction` (`payment_type_id`),
  ADD KEY `FK_Device_Transaction` (`device_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `FK_UserRole_User` (`user_role_id`);

--
-- Indexes for table `user_role`
--
ALTER TABLE `user_role`
  ADD PRIMARY KEY (`user_role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `adjustment`
--
ALTER TABLE `adjustment`
  MODIFY `adjustment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `adjustment_message`
--
ALTER TABLE `adjustment_message`
  MODIFY `adjustment_message_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `banking`
--
ALTER TABLE `banking`
  MODIFY `banking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `cash_control_policies`
--
ALTER TABLE `cash_control_policies`
  MODIFY `policy_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `center_information`
--
ALTER TABLE `center_information`
  MODIFY `center_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `customer_receipt`
--
ALTER TABLE `customer_receipt`
  MODIFY `customer_receipt_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `department`
--
ALTER TABLE `department`
  MODIFY `depart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT for table `device`
--
ALTER TABLE `device`
  MODIFY `device_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `device_type`
--
ALTER TABLE `device_type`
  MODIFY `device_type_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `game`
--
ALTER TABLE `game`
  MODIFY `game_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lane`
--
ALTER TABLE `lane`
  MODIFY `lane_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lane_status`
--
ALTER TABLE `lane_status`
  MODIFY `lane_status_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `modifier`
--
ALTER TABLE `modifier`
  MODIFY `modifier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `modifier_period`
--
ALTER TABLE `modifier_period`
  MODIFY `modifier_period_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `package`
--
ALTER TABLE `package`
  MODIFY `package_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `package_product`
--
ALTER TABLE `package_product`
  MODIFY `package_product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `payment_method`
--
ALTER TABLE `payment_method`
  MODIFY `payment_method_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `payment_type`
--
ALTER TABLE `payment_type`
  MODIFY `payment_type_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `player`
--
ALTER TABLE `player`
  MODIFY `player_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `policies`
--
ALTER TABLE `policies`
  MODIFY `policy_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `product_type`
--
ALTER TABLE `product_type`
  MODIFY `product_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `product_useage`
--
ALTER TABLE `product_useage`
  MODIFY `product_useage_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `shift`
--
ALTER TABLE `shift`
  MODIFY `shift_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sub_department`
--
ALTER TABLE `sub_department`
  MODIFY `sub_depart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `tax_type`
--
ALTER TABLE `tax_type`
  MODIFY `tax_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `terminal`
--
ALTER TABLE `terminal`
  MODIFY `terminal_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `user_role`
--
ALTER TABLE `user_role`
  MODIFY `user_role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `banking`
--
ALTER TABLE `banking`
  ADD CONSTRAINT `FK_Banking_SubDepartment` FOREIGN KEY (`sub_depart_id`) REFERENCES `sub_department` (`sub_depart_id`);

--
-- Constraints for table `department`
--
ALTER TABLE `department`
  ADD CONSTRAINT `FK_User_Department` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `device`
--
ALTER TABLE `device`
  ADD CONSTRAINT `FK_DeviceType_Device` FOREIGN KEY (`device_type_id`) REFERENCES `device_type` (`device_type_id`);

--
-- Constraints for table `game`
--
ALTER TABLE `game`
  ADD CONSTRAINT `FK_PlayerID_Game` FOREIGN KEY (`player_id`) REFERENCES `player` (`player_id`);

--
-- Constraints for table `lane`
--
ALTER TABLE `lane`
  ADD CONSTRAINT `FK_Lane_LaneStatus` FOREIGN KEY (`lane_status_id`) REFERENCES `lane_status` (`lane_status_id`);

--
-- Constraints for table `modifier`
--
ALTER TABLE `modifier`
  ADD CONSTRAINT `FK_Modifier_Product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `package`
--
ALTER TABLE `package`
  ADD CONSTRAINT `FK_Package_SubDepartment` FOREIGN KEY (`sub_depart_id`) REFERENCES `sub_department` (`sub_depart_id`);

--
-- Constraints for table `package_product`
--
ALTER TABLE `package_product`
  ADD CONSTRAINT `FK_PP_Package` FOREIGN KEY (`package_id`) REFERENCES `package` (`package_id`),
  ADD CONSTRAINT `FK_PP_Product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `FK_ProductType_Product` FOREIGN KEY (`product_type_id`) REFERENCES `product_type` (`product_type_id`),
  ADD CONSTRAINT `FK_SubDepartment_Product` FOREIGN KEY (`sub_depart_id`) REFERENCES `sub_department` (`sub_depart_id`),
  ADD CONSTRAINT `FK_TaxType_Product` FOREIGN KEY (`tax_type_id`) REFERENCES `tax_type` (`tax_type_id`);

--
-- Constraints for table `product_useage`
--
ALTER TABLE `product_useage`
  ADD CONSTRAINT `FK_Lane_ProductUseage` FOREIGN KEY (`lane_id`) REFERENCES `lane` (`lane_id`),
  ADD CONSTRAINT `FK_Product_ProductUseage` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `FK_Transaction_ProductUseage` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`transaction_id`);

--
-- Constraints for table `shift`
--
ALTER TABLE `shift`
  ADD CONSTRAINT `FK_User_ShiftUserEnd` FOREIGN KEY (`user_end`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `FK_User_ShiftUserStart` FOREIGN KEY (`user_start`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `sub_department`
--
ALTER TABLE `sub_department`
  ADD CONSTRAINT `FK_DepartmentSubDepartment` FOREIGN KEY (`depart_id`) REFERENCES `department` (`depart_id`),
  ADD CONSTRAINT `FK_User_SubDepartment` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `sub_department_ibfk_1` FOREIGN KEY (`depart_id`) REFERENCES `department` (`depart_id`);

--
-- Constraints for table `tax_type`
--
ALTER TABLE `tax_type`
  ADD CONSTRAINT `FK_TaxType_SubDepartment` FOREIGN KEY (`sub_depart_id`) REFERENCES `sub_department` (`sub_depart_id`);

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `FK_Device_Transaction` FOREIGN KEY (`device_id`) REFERENCES `device` (`device_id`),
  ADD CONSTRAINT `FK_Lane_Transaction` FOREIGN KEY (`lane_id`) REFERENCES `lane` (`lane_id`),
  ADD CONSTRAINT `FK_PaymentType_Transaction` FOREIGN KEY (`payment_type_id`) REFERENCES `payment_type` (`payment_type_id`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `FK_UserRole_User` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`user_role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
