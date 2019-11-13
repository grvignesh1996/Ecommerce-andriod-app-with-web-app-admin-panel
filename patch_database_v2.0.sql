ALTER TABLE `product_order` ADD `serial` VARCHAR(100) NULL DEFAULT NULL AFTER `tax`;

ALTER TABLE `product` ADD `price_discount` DECIMAL(12,2) NOT NULL DEFAULT '0' AFTER `price`;

INSERT INTO `config` (`code`, `value`) VALUES ('EMAIL_NOTIF_ON_ORDER', 'FALSE');
INSERT INTO `config` (`code`, `value`) VALUES ('EMAIL_NOTIF_ON_ORDER_PROCESS', 'FALSE');
INSERT INTO `config` (`code`, `value`) VALUES ('EMAIL_REPLY_TO', 'email.address@domain.com');
INSERT INTO `config` (`code`, `value`) VALUES ('EMAIL_BCC_RECEIVER', '["admin1@domain.com","admin2@domain.com"]');
