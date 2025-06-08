<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

if ( ! function_exists( 'webriti_companion_rambo_customize_register' ) ) :
	/**
	 * Quality Companion Customize Register
	 */
	function webriti_companion_rambo_customize_register( $wp_customize ) {
		$rambo_features_content_control = $wp_customize->get_setting( 'rambo_pro_theme_options[rambo_service_content]' );
		if ( ! empty( $rambo_features_content_control ) ) {
			$rambo_features_content_control->default = rambo_get_service_default();
		}
	}
	add_action( 'customize_register', 'webriti_companion_rambo_customize_register' ,9);
endif;