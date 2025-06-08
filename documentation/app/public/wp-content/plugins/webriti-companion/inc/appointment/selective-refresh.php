<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

function webriti_companion_ap_customize_selective_refresh( $wp_customize ) {
    if ( ! isset( $wp_customize->selective_refresh ) ) return;
    // Header Social Icons
    $wp_customize->selective_refresh->add_partial('appointment_options[slider_select_category]',
        array(
            'selector'  => '.slide-text-bg1 h2',
            'settings'  => 'appointment_options[slider_select_category]'
        )
    );
    // Service Section Heading
    $wp_customize->selective_refresh->add_partial('appointment_options[service_title]',
        array(
            'selector'  => '.Service-section .section-heading-title h1',
            'settings'  => 'appointment_options[service_title]'
        )
    );
    // Service Section Description
    $wp_customize->selective_refresh->add_partial('appointment_options[service_description]',
        array(
            'selector'  => '.Service-section .section-heading-title p',
            'settings'  => 'appointment_options[service_description]'
        )
    );
    // Home Callout Section Heading
    $wp_customize->selective_refresh->add_partial('appointment_options[home_call_out_title]',
        array(
            'selector'  => '.callout-section h2',
            'settings'  => 'appointment_options[home_call_out_title]'
        )
    );
    // Home Callout Section Description
    $wp_customize->selective_refresh->add_partial('appointment_options[home_call_out_description]',
        array(
            'selector'  => '.callout-section p',
            'settings'  => 'appointment_options[home_call_out_description]'
        )
    );
    // Home Callout btn1
    $wp_customize->selective_refresh->add_partial('appointment_options[home_call_out_btn1_text]',
        array(
            'selector'  => '.callout-section .callout-btn1',
            'settings'  => 'appointment_options[home_call_out_btn1_text]'
        )
    );
    // Home Callout btn2
    $wp_customize->selective_refresh->add_partial('appointment_options[home_call_out_btn2_text]',
        array(
            'selector'  => '.callout-section .callout-btn2',
            'settings'  => 'appointment_options[home_call_out_btn2_text]'
        )
    );

}
add_action( 'customize_register', 'webriti_companion_ap_customize_selective_refresh' );