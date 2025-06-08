<?php
/**
 * The base configuration for WordPress
 *
 * The wp-config.php creation script uses this file during the installation.
 * You don't have to use the web site, you can copy this file to "wp-config.php"
 * and fill in the values.
 *
 * This file contains the following configurations:
 *
 * * Database settings
 * * Secret keys
 * * Database table prefix
 * * Localized language
 * * ABSPATH
 *
 * @link https://wordpress.org/support/article/editing-wp-config-php/
 *
 * @package WordPress
 */

// ** Database settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define( 'DB_NAME', 'local' );

/** Database username */
define( 'DB_USER', 'root' );

/** Database password */
define( 'DB_PASSWORD', 'root' );

/** Database hostname */
define( 'DB_HOST', 'localhost' );

/** Database charset to use in creating database tables. */
define( 'DB_CHARSET', 'utf8' );

/** The database collate type. Don't change this if in doubt. */
define( 'DB_COLLATE', '' );

/**#@+
 * Authentication unique keys and salts.
 *
 * Change these to different unique phrases! You can generate these using
 * the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}.
 *
 * You can change these at any point in time to invalidate all existing cookies.
 * This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define( 'AUTH_KEY',          '0huqc}uzW5`1-Q2nEd%P_q{ijgX`K(c2oGRX#-UKdOG4M0+4O;zN@=g:^mwvGgj{' );
define( 'SECURE_AUTH_KEY',   'OC<1_nUa:U-=&:}fg[AN}d).Y3&)-:DN_Zn.|/ueUw[TTAvmD6V ]#X}@.Jql_TV' );
define( 'LOGGED_IN_KEY',     '*~ uQp6$4$.;;i2E_MdkfG#{OpdPfFayNFdX@eT7z?o8H[Ck{twH#Ly<bGVW%{8C' );
define( 'NONCE_KEY',         '2g3,_fH-1T*?/7V. 5$gOb..P3ovx+(%.;a%Pkf354{kaYIXSE}w,gX9ea82tx>`' );
define( 'AUTH_SALT',         '>PX_SN>A:X]Qu81=(b5Z&iVV)5BMbp>8|E]xq[Id;hU$$k9GjzT1yN0-0V2G+f<h' );
define( 'SECURE_AUTH_SALT',  '!z%`hY?w]@X@Mp|5oGI>?ulC=@)-H{G7*3kai-7G[67jTos6 |<|ui4YqaZP<5EU' );
define( 'LOGGED_IN_SALT',    '::K`-x2z/<bi]T?H(#IsV?g5%{5e1.DbzR$R;5E]wNb9moy`:Im_+ @f&/#H?gcb' );
define( 'NONCE_SALT',        'ak9myl?6r~AK?G!)HA&2Y9 Y{G^bgUH&%_0/9&i_!wUl{1$,g0v&)ro**Qwgx<!Q' );
define( 'WP_CACHE_KEY_SALT', '1|zZS>~6g9EUvyb+Ot>m3<2 >AB(`z=`pvH^bR9qHF4kdx*Iedek8BeM#iqB|wkM' );


/**#@-*/

/**
 * WordPress database table prefix.
 *
 * You can have multiple installations in one database if you give each
 * a unique prefix. Only numbers, letters, and underscores please!
 */
$table_prefix = 'wp_';


/* Add any custom values between this line and the "stop editing" line. */



/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 *
 * For information on other constants that can be used for debugging,
 * visit the documentation.
 *
 * @link https://wordpress.org/support/article/debugging-in-wordpress/
 */
if ( ! defined( 'WP_DEBUG' ) ) {
	define( 'WP_DEBUG', false );
}

define( 'WP_ENVIRONMENT_TYPE', 'local' );
/* That's all, stop editing! Happy publishing. */

/** Absolute path to the WordPress directory. */
if ( ! defined( 'ABSPATH' ) ) {
	define( 'ABSPATH', __DIR__ . '/' );
}

/** Sets up WordPress vars and included files. */
require_once ABSPATH . 'wp-settings.php';
