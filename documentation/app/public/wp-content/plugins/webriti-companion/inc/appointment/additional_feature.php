<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

add_action('widgets_init', 'webriti_companion_appointment_orange_sidebar_init');

if (!function_exists('webriti_companion_appointment_orange_sidebar_init')) {

    //orange sidebar
    register_sidebar(array(
        'name' => esc_html__('Left widget area below slider', 'webriti-companion'),
        'id' => 'home-orange-sidebar_left',
        'description' => esc_html__('Left widget area below slider', 'webriti-companion'),
        'before_widget' => '<div class="left-callout">',
        'after_widget' => '</div>',
        'before_title' => '<h3>',
        'after_title' => '</h3>',
    ));
    register_sidebar(array(
        'name' => esc_html__('Right widget area below slider', 'webriti-companion'),
        'id' => 'home-orange-sidebar_right',
        'description' => esc_html__('Right widget area below slider', 'webriti-companion'),
        'before_widget' => '<div class="right-callout">',
        'after_widget' => '</div>',
        'before_title' => '<h3>',
        'after_title' => '</h3>',
    ));
    register_sidebar(array(
        'name' => esc_html__('Center widget area below slider', 'webriti-companion'),
        'id' => 'home-orange-sidebar_center',
        'description' => esc_html__('Center widget area below slider', 'webriti-companion'),
        'before_widget' => '<div class="center-callout">',
        'after_widget' => '</div>',
        'before_title' => '<h3>',
        'after_title' => '</h3>',
    ));
}