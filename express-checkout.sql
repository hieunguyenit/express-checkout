USE mca_checkout;

DROP TABLE IF EXISTS `checkout`;

CREATE TABLE `checkout` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`checkout_id` varchar(20) NOT NULL,
`merchant_id` varchar(100) NOT NULL,
`method` varchar(30) NOT NULL,
`return_url` varchar(255) NOT NULL,
`cancel_url` varchar(255) NOT NULL,
`status` varchar(30) NOT NULL,
`merchant_info` text NOT NULL,
`invoice_info` text NOT NULL,
`customer_info` text DEFAULT NULL,
`transaction_info` text DEFAULT NULL,
`created_at` datetime NOT NULL,
`updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `settlement`;

CREATE TABLE `settlement` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`checkout_id` varchar(20) NOT NULL,
`xtran_id` varchar(20) NOT NULL,
`amount` int not null,
`done` bool not null,
`created_at` datetime NOT NULL,
`updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
