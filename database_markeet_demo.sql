-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 08, 2017 at 09:10 AM
-- Server version: 10.1.16-MariaDB
-- PHP Version: 7.0.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `markeet_demo`
--

-- --------------------------------------------------------

--
-- Table structure for table `app_version`
--

CREATE TABLE `app_version` (
  `id` bigint(20) NOT NULL,
  `version_code` int(11) NOT NULL,
  `version_name` varchar(50) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `app_version`
--

INSERT INTO `app_version` (`id`, `version_code`, `version_name`, `active`, `created_at`, `last_update`) VALUES
(1, 1, '1', 1, 1484386275760, 1485795959275),
(2, 2, '2', 1, 1485795799112, 1485795799112);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `icon` varchar(100) NOT NULL,
  `draft` tinyint(1) NOT NULL,
  `brief` varchar(100) NOT NULL,
  `color` varchar(7) NOT NULL,
  `priority` int(11) NOT NULL DEFAULT '0',
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `icon`, `draft`, `brief`, `color`, `priority`, `created_at`, `last_update`) VALUES
(3, 'Service and Food', 'Service & Food.png', 0, 'Tickets and services, airlines, food and drinks.', '#ff6f52', 7, 1485609656784, 1485623552964),
(4, 'Sports and Hobby', 'Sports & Hobby.png', 0, 'Sports and outdoor, hobby, books, automotive.', '#8ddd6f', 6, 1485609722495, 1485623540625);

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `code` varchar(50) NOT NULL,
  `value` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`code`, `value`) VALUES
('CURRENCY', 'USD'),
('FEATURED_NEWS', '5'),
('SHIPPING', '["FEDEX","DHL","TNT"]'),
('TAX', '11'), 
('EMAIL_NOTIF_ON_ORDER', 'FALSE'),
('EMAIL_NOTIF_ON_ORDER_PROCESS', 'FALSE'),
('EMAIL_SENDER', 'your.email@domain.com'),
('EMAIL_SENDER_PASS', 'xxxxxx'),
('EMAIL_REPLY_TO', 'email.address@domain.com'),
('EMAIL_BCC_RECEIVER', '["admin1@domain.com","admin2@domain.com"]');

-- --------------------------------------------------------

--
-- Table structure for table `currency`
--

CREATE TABLE `currency` (
  `id` bigint(20) NOT NULL,
  `code` varchar(3) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `currency`
--

INSERT INTO `currency` (`id`, `code`, `name`) VALUES
(1, 'AFA', 'Afghanistan afghani'),
(2, 'ALL', 'Albanian lek'),
(3, 'DZD', 'Algerian dinar'),
(4, 'AOR', 'Angolan kwanza reajustado'),
(5, 'ARS', 'Argentine peso'),
(6, 'AMD', 'Armenian dram'),
(7, 'AWG', 'Aruban guilder'),
(8, 'AUD', 'Australian dollar'),
(9, 'AZN', 'Azerbaijanian new manat'),
(10, 'BSD', 'Bahamian dollar'),
(11, 'BHD', 'Bahraini dinar'),
(12, 'BDT', 'Bangladeshi taka'),
(13, 'BBD', 'Barbados dollar'),
(14, 'BYN', 'Belarusian ruble'),
(15, 'BZD', 'Belize dollar'),
(16, 'BMD', 'Bermudian dollar'),
(17, 'BTN', 'Bhutan ngultrum'),
(18, 'BOB', 'Bolivian boliviano'),
(19, 'BWP', 'Botswana pula'),
(20, 'BRL', 'Brazilian real'),
(21, 'GBP', 'British pound'),
(22, 'BND', 'Brunei dollar'),
(23, 'BGN', 'Bulgarian lev'),
(24, 'BIF', 'Burundi franc'),
(25, 'KHR', 'Cambodian riel'),
(26, 'CAD', 'Canadian dollar'),
(27, 'CVE', 'Cape Verde escudo'),
(28, 'KYD', 'Cayman Islands dollar'),
(29, 'XOF', 'CFA franc BCEAO'),
(30, 'XAF', 'CFA franc BEAC'),
(31, 'XPF', 'CFP franc'),
(32, 'CLP', 'Chilean peso'),
(33, 'CNY', 'Chinese yuan renminbi'),
(34, 'COP', 'Colombian peso'),
(35, 'KMF', 'Comoros franc'),
(36, 'CDF', 'Congolese franc'),
(37, 'CRC', 'Costa Rican colon'),
(38, 'HRK', 'Croatian kuna'),
(39, 'CUP', 'Cuban peso'),
(40, 'CZK', 'Czech koruna'),
(41, 'DKK', 'Danish krone'),
(42, 'DJF', 'Djibouti franc'),
(43, 'DOP', 'Dominican peso'),
(44, 'XCD', 'East Caribbean dollar'),
(45, 'EGP', 'Egyptian pound'),
(46, 'SVC', 'El Salvador colon'),
(47, 'ERN', 'Eritrean nakfa'),
(48, 'EEK', 'Estonian kroon'),
(49, 'ETB', 'Ethiopian birr'),
(50, 'EUR', 'EU euro'),
(51, 'FKP', 'Falkland Islands pound'),
(52, 'FJD', 'Fiji dollar'),
(53, 'GMD', 'Gambian dalasi'),
(54, 'GEL', 'Georgian lari'),
(55, 'GHS', 'Ghanaian new cedi'),
(56, 'GIP', 'Gibraltar pound'),
(57, 'XAU', 'Gold (ounce)'),
(58, 'XFO', 'Gold franc'),
(59, 'GTQ', 'Guatemalan quetzal'),
(60, 'GNF', 'Guinean franc'),
(61, 'GYD', 'Guyana dollar'),
(62, 'HTG', 'Haitian gourde'),
(63, 'HNL', 'Honduran lempira'),
(64, 'HKD', 'Hong Kong SAR dollar'),
(65, 'HUF', 'Hungarian forint'),
(66, 'ISK', 'Icelandic krona'),
(67, 'XDR', 'IMF special drawing right'),
(68, 'INR', 'Indian rupee'),
(69, 'IDR', 'Indonesian rupiah'),
(70, 'IRR', 'Iranian rial'),
(71, 'IQD', 'Iraqi dinar'),
(72, 'ILS', 'Israeli new shekel'),
(73, 'JMD', 'Jamaican dollar'),
(74, 'JPY', 'Japanese yen'),
(75, 'JOD', 'Jordanian dinar'),
(76, 'KZT', 'Kazakh tenge'),
(77, 'KES', 'Kenyan shilling'),
(78, 'KWD', 'Kuwaiti dinar'),
(79, 'KGS', 'Kyrgyz som'),
(80, 'LAK', 'Lao kip'),
(81, 'LVL', 'Latvian lats'),
(82, 'LBP', 'Lebanese pound'),
(83, 'LSL', 'Lesotho loti'),
(84, 'LRD', 'Liberian dollar'),
(85, 'LYD', 'Libyan dinar'),
(86, 'LTL', 'Lithuanian litas'),
(87, 'MOP', 'Macao SAR pataca'),
(88, 'MKD', 'Macedonian denar'),
(89, 'MGA', 'Malagasy ariary'),
(90, 'MWK', 'Malawi kwacha'),
(91, 'MYR', 'Malaysian ringgit'),
(92, 'MVR', 'Maldivian rufiyaa'),
(93, 'MRO', 'Mauritanian ouguiya'),
(94, 'MUR', 'Mauritius rupee'),
(95, 'MXN', 'Mexican peso'),
(96, 'MDL', 'Moldovan leu'),
(97, 'MNT', 'Mongolian tugrik'),
(98, 'MAD', 'Moroccan dirham'),
(99, 'MZN', 'Mozambique new metical'),
(100, 'MMK', 'Myanmar kyat'),
(101, 'NAD', 'Namibian dollar'),
(102, 'NPR', 'Nepalese rupee'),
(103, 'ANG', 'Netherlands Antillian guilder'),
(104, 'NZD', 'New Zealand dollar'),
(105, 'NIO', 'Nicaraguan cordoba oro'),
(106, 'NGN', 'Nigerian naira'),
(107, 'KPW', 'North Korean won'),
(108, 'NOK', 'Norwegian krone'),
(109, 'OMR', 'Omani rial'),
(110, 'PKR', 'Pakistani rupee'),
(111, 'XPD', 'Palladium (ounce)'),
(112, 'PAB', 'Panamanian balboa'),
(113, 'PGK', 'Papua New Guinea kina'),
(114, 'PYG', 'Paraguayan guarani'),
(115, 'PEN', 'Peruvian nuevo sol'),
(116, 'PHP', 'Philippine peso'),
(117, 'XPT', 'Platinum (ounce)'),
(118, 'PLN', 'Polish zloty'),
(119, 'QAR', 'Qatari rial'),
(120, 'RON', 'Romanian new leu'),
(121, 'RUB', 'Russian ruble'),
(122, 'RWF', 'Rwandan franc'),
(123, 'SHP', 'Saint Helena pound'),
(124, 'WST', 'Samoan tala'),
(125, 'STD', 'Sao Tome and Principe dobra'),
(126, 'SAR', 'Saudi riyal'),
(127, 'RSD', 'Serbian dinar'),
(128, 'SCR', 'Seychelles rupee'),
(129, 'SLL', 'Sierra Leone leone'),
(130, 'XAG', 'Silver (ounce)'),
(131, 'SGD', 'Singapore dollar'),
(132, 'SBD', 'Solomon Islands dollar'),
(133, 'SOS', 'Somali shilling'),
(134, 'ZAR', 'South African rand'),
(135, 'KRW', 'South Korean won'),
(136, 'LKR', 'Sri Lanka rupee'),
(137, 'SDG', 'Sudanese pound'),
(138, 'SRD', 'Suriname dollar'),
(139, 'SZL', 'Swaziland lilangeni'),
(140, 'SEK', 'Swedish krona'),
(141, 'CHF', 'Swiss franc'),
(142, 'SYP', 'Syrian pound'),
(143, 'TWD', 'Taiwan New dollar'),
(144, 'TJS', 'Tajik somoni'),
(145, 'TZS', 'Tanzanian shilling'),
(146, 'THB', 'Thai baht'),
(147, 'TOP', 'Tongan paanga'),
(148, 'TTD', 'Trinidad and Tobago dollar'),
(149, 'TND', 'Tunisian dinar'),
(150, 'TRY', 'Turkish lira'),
(151, 'TMT', 'Turkmen new manat'),
(152, 'AED', 'UAE dirham'),
(153, 'UGX', 'Uganda new shilling'),
(154, 'XFU', 'UIC franc'),
(155, 'UAH', 'Ukrainian hryvnia'),
(156, 'UYU', 'Uruguayan peso uruguayo'),
(157, 'USD', 'US dollar'),
(158, 'UZS', 'Uzbekistani sum'),
(159, 'VUV', 'Vanuatu vatu'),
(160, 'VEF', 'Venezuelan bolivar fuerte'),
(161, 'VND', 'Vietnamese dong'),
(162, 'YER', 'Yemeni rial'),
(163, 'ZMK', 'Zambian kwacha'),
(164, 'ZWL', 'Zimbabwe dollar');

-- --------------------------------------------------------

--
-- Table structure for table `fcm`
--

CREATE TABLE `fcm` (
  `id` bigint(20) NOT NULL,
  `device` varchar(100) NOT NULL,
  `os_version` varchar(100) NOT NULL,
  `app_version` varchar(10) NOT NULL,
  `serial` varchar(100) NOT NULL,
  `regid` text NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `fcm`
--

INSERT INTO `fcm` (`id`, `device`, `os_version`, `app_version`, `serial`, `regid`, `created_at`, `last_update`) VALUES
(4, 'Device Name', '6.0.1', '1.0', 'GGC00C0888E426A', 'APA91bEj7qmlVePXUpG4UjKOtyqG5x9hpeZ4tMhPDsJgDRWL76psPGtckLK3uMtmpLFj3RSFfgaVoBMCKhg5iR8RnPZPjeuml8Llgkc', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `news_info`
--

CREATE TABLE `news_info` (
  `id` bigint(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `brief_content` varchar(200) NOT NULL,
  `full_content` text NOT NULL,
  `image` varchar(110) NOT NULL,
  `draft` tinyint(1) NOT NULL,
  `status` varchar(50) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `news_info`
--

INSERT INTO `news_info` (`id`, `title`, `brief_content`, `full_content`, `image`, `draft`, `status`, `created_at`, `last_update`) VALUES
(1, 'Available Best Interior Stuff, Browse and Discover Now for Your Room.', 'Quisque efficitur diam sit amet quam porttitor, at dictum arcu viverra. Vivamus dapibus ante nunc, non malesuada enim gravida in.', '<div>Aliquam sed lorem quis urna eleifend aliquam ac ut urna. Sed tempus nisl tellus, vel tempor velit rutrum ac. Ut facilisis eget augue quis pulvinar. Nam a felis eu tortor convallis dapibus efficitur vel turpis. Etiam hendrerit pulvinar leo ac venenatis. Donec non felis et massa semper convallis nec id augue. Proin sit amet mollis nisl. Sed vestibulum auctor imperdiet. Nam ut enim non lacus blandit rhoncus non at turpis. Donec vel arcu malesuada, vulputate dui a, varius leo. Proin volutpat libero ultricies est auctor, eget facilisis ligula accumsan. Sed quis sapien hendrerit, luctus augue nec, aliquam urna. Mauris vestibulum convallis malesuada. Sed et ex cursus, fringilla tellus ac, maximus ipsum.</div><div><br></div><div>Sed viverra arcu in neque pulvinar dignissim. Nunc euismod aliquam magna, non efficitur lectus vehicula quis. Suspendisse vitae consequat arcu, eget malesuada dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec quis imperdiet libero. Donec imperdiet quis lectus vitae dignissim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam pulvinar pretium ipsum, et faucibus libero aliquet et. Quisque efficitur diam sit amet quam porttitor, at dictum arcu viverra. Vivamus dapibus ante nunc, non malesuada enim gravida in. Cras bibendum eros vitae lacus efficitur, vel pretium est rutrum.</div><div><br></div><div>Praesent id efficitur risus. Nullam dui ligula, eleifend in convallis at, porta vitae massa. Curabitur sapien lorem, congue non enim non, rutrum gravida ipsum. Duis dignissim sapien in sem finibus ultrices id nec justo. Nulla hendrerit sed eros ac rhoncus. Nullam varius tellus id venenatis cursus. Cras commodo metus mauris, eget posuere sapien blandit non. Aenean fermentum ligula a libero suscipit dictum. Nulla dictum odio ut vulputate aliquam. Suspendisse potenti. Cras pellentesque vel felis sed dignissim. Proin aliquet orci tincidunt dui convallis, facilisis accumsan urna semper.</div>', 'Sed tempus nisl tellus vel tempor velit rutrum ac Ut facilisis eget augue quis pulvinar.png', 0, 'FEATURED', 1482250793274, 1485795107581),
(2, 'We Join Smartphone Fair  in Washington, D.C. April 7-8, 2017. Visit and Purchase our Product.', 'Aenean eu libero eget lorem tincidunt eleifend. Integer in diam felis. Proin sit amet mollis nisl.', '<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec scelerisque urna ut metus sagittis, vel congue lorem iaculis. Sed hendrerit mauris id tempor faucibus. In facilisis nibh vulputate ante porttitor, et venenatis arcu placerat. Vestibulum tempor nisi enim, eget consectetur ex accumsan fermentum. Mauris ipsum lacus, imperdiet eget purus vel, convallis euismod augue. In sed fringilla sem. Aenean ultricies ullamcorper euismod.</div><div><br></div><div>Cras non nunc est. Nunc quis dapibus lorem. Proin dignissim interdum interdum. In vitae aliquet odio. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam non venenatis nunc, id molestie velit. Vestibulum sagittis dui at nibh lacinia faucibus. Curabitur auctor sem diam. Aenean eu libero eget lorem tincidunt eleifend. Integer in diam felis.</div><div><br></div><div>Aliquam sed lorem quis urna eleifend aliquam ac ut urna. Sed tempus nisl tellus, vel tempor velit rutrum ac. Ut facilisis eget augue quis pulvinar. Nam a felis eu tortor convallis dapibus efficitur vel turpis. Etiam hendrerit pulvinar leo ac venenatis. Donec non felis et massa semper convallis nec id augue. Proin sit amet mollis nisl. Sed vestibulum auctor imperdiet. Nam ut enim non lacus blandit rhoncus non at turpis. Donec vel arcu malesuada, vulputate dui a, varius leo. Proin volutpat libero ultricies est auctor, eget facilisis ligula accumsan. Sed quis sapien hendrerit, luctus augue nec, aliquam urna. Mauris vestibulum convallis malesuada. Sed et ex cursus, fringilla tellus ac, maximus ipsum.</div>', 'Quisque efficitur diam sit amet quam porttitor at dictum arcu viverra.png', 0, 'FEATURED', 1485793148268, 1485794701501),
(10, 'Unique Women''s Accessories Trend that Will Popular Current Season', 'Cras in dapibus ligula. Vestibulum elementum ante at sapien consectetur porta.', '<div>Cras in dapibus ligula. Vestibulum elementum ante at sapien consectetur porta. Nulla laoreet mauris a orci posuere eleifend. Duis dictum nisl scelerisque lectus tincidunt, id tristique lorem eleifend. Curabitur nibh risus, commodo in consequat eget, consectetur vel tellus. Suspendisse dignissim, lorem ut luctus congue, mi justo lobortis ex, non suscipit est est id urna. Ut eget tortor accumsan, consectetur justo sit amet, tincidunt enim.</div><div><br></div><div>Proin efficitur justo eget tempus vulputate. Nunc viverra felis tortor, a porttitor tortor vehicula dictum. Nam quis sapien nec tellus ultricies dictum. Praesent vulputate dolor sed nisl consectetur, vel varius dui suscipit. Maecenas vulputate ligula a sem sollicitudin viverra. In a libero viverra, eleifend nisi vitae, vehicula arcu. Donec sagittis sem sit amet nulla aliquam, a convallis risus ultricies. Praesent tristique magna odio, a dictum felis hendrerit quis.</div>', 'Unique Womens Accessories Trend that Will Popular Current Season.jpg', 0, 'NORMAL', 1485992959638, 1485992959638);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `image` varchar(110) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `price_discount` DECIMAL(12,2) NOT NULL DEFAULT '0',
  `stock` int(10) NOT NULL,
  `draft` tinyint(1) NOT NULL,
  `description` text NOT NULL,
  `status` varchar(50) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `image`, `price`, `stock`, `draft`, `description`, `status`, `created_at`, `last_update`) VALUES
(3, 'Derek Heart Juniors', 'Derek Heart Juniors.jpg', '23.00', 90, 0, '<div>Sed viverra arcu in neque pulvinar dignissim. Nunc euismod aliquam magna, non efficitur lectus vehicula quis. Suspendisse vitae consequat arcu, eget malesuada dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec quis imperdiet libero. Donec imperdiet quis lectus vitae dignissim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam pulvinar pretium ipsum, et faucibus libero aliquet et. Quisque efficitur diam sit amet quam porttitor, at dictum arcu viverra. Vivamus dapibus ante nunc, non malesuada enim gravida in. Cras bibendum eros vitae lacus efficitur, vel pretium est rutrum.</div><div><br></div><div>Praesent id efficitur risus. Nullam dui ligula, eleifend in convallis at, porta vitae massa. Curabitur sapien lorem, congue non enim non, rutrum gravida ipsum. Duis dignissim sapien in sem finibus ultrices id nec justo. Nulla hendrerit sed eros ac rhoncus. Nullam varius tellus id venenatis cursus. Cras commodo metus mauris, eget posuere sapien blandit non. Aenean fermentum ligula a libero suscipit dictum. Nulla dictum odio ut vulputate aliquam. Suspendisse potenti. Cras pellentesque vel felis sed dignissim. Proin aliquet orci tincidunt dui convallis, facilisis accumsan urna semper.</div>', 'READY STOCK', 1485624280740, 1486541362705),
(20, 'NXY Soft Matte Lip Cream', 'NXY Soft Matte Lip Cream.jpg', '10.00', 90, 0, '<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec scelerisque urna ut metus sagittis, vel congue lorem iaculis. Sed hendrerit mauris id tempor faucibus. In facilisis nibh vulputate ante porttitor, et venenatis arcu placerat. Vestibulum tempor nisi enim, eget consectetur ex accumsan fermentum. Mauris ipsum lacus, imperdiet eget purus vel, convallis euismod augue. In sed fringilla sem. Aenean ultricies ullamcorper euismod.</div><div><br></div><div>Cras non nunc est. Nunc quis dapibus lorem. Proin dignissim interdum interdum. In vitae aliquet odio. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam non venenatis nunc, id molestie velit. Vestibulum sagittis dui at nibh lacinia faucibus. Curabitur auctor sem diam. Aenean eu libero eget lorem tincidunt eleifend. Integer in diam felis.</div>', 'READY STOCK', 1485624280740, 1486541352723),
(49, 'Sofa Living Room Recliner Nordvalla Medium', 'Sofa Living Room Recliner Nordvalla Medium.jpg', '10.00', 90, 0, '<div>Sed viverra arcu in neque pulvinar dignissim. Nunc euismod aliquam magna, non efficitur lectus vehicula quis. Suspendisse vitae consequat arcu, eget malesuada dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec quis imperdiet libero. Donec imperdiet quis lectus vitae dignissim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam pulvinar pretium ipsum, et faucibus libero aliquet et. Quisque efficitur diam sit amet quam porttitor, at dictum arcu viverra. Vivamus dapibus ante nunc, non malesuada enim gravida in. Cras bibendum eros vitae lacus efficitur, vel pretium est rutrum.</div><div><br></div><div>Praesent id efficitur risus. Nullam dui ligula, eleifend in convallis at, porta vitae massa. Curabitur sapien lorem, congue non enim non, rutrum gravida ipsum. Duis dignissim sapien in sem finibus ultrices id nec justo. Nulla hendrerit sed eros ac rhoncus. Nullam varius tellus id venenatis cursus. Cras commodo metus mauris, eget posuere sapien blandit non. Aenean fermentum ligula a libero suscipit dictum. Nulla dictum odio ut vulputate aliquam. Suspendisse potenti. Cras pellentesque vel felis sed dignissim. Proin aliquet orci tincidunt dui convallis, facilisis accumsan urna semper.</div>', 'READY STOCK', 1485624280740, 1486541338627),
(78, 'iPhone 6 - Black', 'iPhone 6  Black.jpg', '10.00', 90, 0, '<div>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec scelerisque urna ut metus sagittis, vel congue lorem iaculis. Sed hendrerit mauris id tempor faucibus. In facilisis nibh vulputate ante porttitor, et venenatis arcu placerat. Vestibulum tempor nisi enim, eget consectetur ex accumsan fermentum. Mauris ipsum lacus, imperdiet eget purus vel, convallis euismod augue. In sed fringilla sem. Aenean ultricies ullamcorper euismod.</div><div><br></div><div>Cras non nunc est. Nunc quis dapibus lorem. Proin dignissim interdum interdum. In vitae aliquet odio. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam non venenatis nunc, id molestie velit. Vestibulum sagittis dui at nibh lacinia faucibus. Curabitur auctor sem diam. Aenean eu libero eget lorem tincidunt eleifend. Integer in diam felis.</div>', 'READY STOCK', 1485624280740, 1486541321906);

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

CREATE TABLE `product_category` (
  `product_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product_category`
--

INSERT INTO `product_category` (`product_id`, `category_id`) VALUES
(78, 4),
(49, 3),
(49, 4),
(20, 3),
(3, 3),
(3, 4);

-- --------------------------------------------------------

--
-- Table structure for table `product_image`
--

CREATE TABLE `product_image` (
  `product_id` bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `product_order`
--

CREATE TABLE `product_order` (
  `id` bigint(20) NOT NULL,
  `code` varchar(20) NOT NULL,
  `buyer` varchar(50) NOT NULL,
  `address` varchar(300) NOT NULL,
  `email` varchar(50) NOT NULL,
  `shipping` varchar(20) NOT NULL,
  `date_ship` bigint(20) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `comment` text NOT NULL,
  `status` varchar(50) NOT NULL,
  `total_fees` decimal(12,2) NOT NULL,
  `tax` decimal(12,2) NOT NULL,
  `serial` VARCHAR(100) NULL DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `product_order_detail`
--

CREATE TABLE `product_order_detail` (
  `id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `amount` int(11) NOT NULL,
  `price_item` decimal(12,2) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` bigint(11) NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 NOT NULL,
  `username` varchar(20) CHARACTER SET utf8 NOT NULL,
  `email` varchar(50) CHARACTER SET utf8 NOT NULL,
  `password` text CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `name`, `username`, `email`, `password`) VALUES
(1, 'User Markeet', 'user_markeet', 'user@mail.com', 'ee11cbb19052e40b07aac0ca060c23ee');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `app_version`
--
ALTER TABLE `app_version`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique` (`name`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`code`);

--
-- Indexes for table `currency`
--
ALTER TABLE `currency`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fcm`
--
ALTER TABLE `fcm`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `news_info`
--
ALTER TABLE `news_info`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `product_unique_name` (`name`);

--
-- Indexes for table `product_category`
--
ALTER TABLE `product_category`
  ADD KEY `fk_product_category_1` (`product_id`),
  ADD KEY `fk_product_category_2` (`category_id`);

--
-- Indexes for table `product_image`
--
ALTER TABLE `product_image`
  ADD KEY `fk_table_images` (`product_id`);

--
-- Indexes for table `product_order`
--
ALTER TABLE `product_order`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product_order_detail`
--
ALTER TABLE `product_order_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_table_orders_item` (`order_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_username` (`username`),
  ADD UNIQUE KEY `unique_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `app_version`
--
ALTER TABLE `app_version`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `currency`
--
ALTER TABLE `currency`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=165;
--
-- AUTO_INCREMENT for table `fcm`
--
ALTER TABLE `fcm`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `news_info`
--
ALTER TABLE `news_info`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=122;
--
-- AUTO_INCREMENT for table `product_order`
--
ALTER TABLE `product_order`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `product_order_detail`
--
ALTER TABLE `product_order_detail`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `product_category`
--
ALTER TABLE `product_category`
  ADD CONSTRAINT `fk_product_category_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_product_category_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `product_image`
--
ALTER TABLE `product_image`
  ADD CONSTRAINT `fk_table_images` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `product_order_detail`
--
ALTER TABLE `product_order_detail`
  ADD CONSTRAINT `fk_table_orders_item` FOREIGN KEY (`order_id`) REFERENCES `product_order` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
