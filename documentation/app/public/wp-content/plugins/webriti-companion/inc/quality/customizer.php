<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

if (!function_exists('webriti_companion_quality_customize_register')) :
    /**
     * Quality Companion Customize Register
     */
    function webriti_companion_quality_customize_register($wp_customize) {
        $quality_features_content_control = $wp_customize->get_setting('quality_pro_options[quality_service_content]');
        if (!empty($quality_features_content_control)) {
            $quality_features_content_control->default = webriti_companion_quality_get_service_default();
        }
    }
    add_action('customize_register', 'webriti_companion_quality_customize_register');
endif;

# This function added in case if a user directly update -
# the plugin before update the parent Quality theme

if (!function_exists('webriti_companion_quality_theme_data_setup')) {

    function webriti_companion_quality_theme_data_setup() {
        return $theme_options = array(
            //Logo and Fevicon header			
            'home_feature' => QUALITY_TEMPLATE_DIR_URI . '/images/slider/slide.jpg',
            'upload_image_logo' => '',
            'height' => '80',
            'width' => '200',
            'text_title' => false,
            'upload_image_favicon' => '',
            'style_sheet' => 'default.css',
            /* Home Image */
            'slider_enable' => true,
            'home_image_title' => esc_html__('Lorem Ipsum', 'webriti-companion'),
            'home_image_sub_title' => esc_html__('Welcome to Quality theme', 'webriti-companion'),
            'home_image_description' => esc_html__('Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin pharetra volutpat vehicula.', 'webriti-companion'),
            'home_image_button_text' => esc_html__('Etiam sit amet', 'webriti-companion'),
            'home_image_button_link' => 'https://webriti.com/quality-lite-version-details-page/',
            'service_title' => esc_html__('Nam suscipit libero', 'webriti-companion'),
            'service_description' => esc_html__('Lorem Ipsum which looks reason able. The generated Lorem Ipsum is therefore always free.', 'webriti-companion'),
            'service_enable' => true,
            'service_one_title' => esc_html__('Donec tristique', 'webriti-companion'),
            'service_two_title' => esc_html__('Donec tristique', 'webriti-companion'),
            'service_three_title' => esc_html__('Donec tristique', 'webriti-companion'),
            'service_four_title' => esc_html__('Donec tristique', 'webriti-companion'),
            'service_one_icon' => 'fa fa-shield',
            'service_two_icon' => 'fa fa-tablet',
            'service_three_icon' => 'fa fa-edit',
            'service_four_icon' => 'fa fa-star-half-o',
            'service_one_text' => esc_html__('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.', 'webriti-companion'),
            'service_two_text' => esc_html__('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.', 'webriti-companion'),
            'service_three_text' => esc_html__('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.', 'webriti-companion'),
            'service_four_text' => esc_html__('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.', 'webriti-companion'),
            //Projects Section Settings
            'home_projects_enabled' => true,
            'project_heading_one' => esc_html__('Proin tincidunt tincidunt', 'webriti-companion'),
            'project_tagline' => esc_html__('Scelerisque eu lectus.', 'webriti-companion'),
            'project_one_thumb' => QUALITY_TEMPLATE_DIR_URI . '/images/project_thumb.png',
            'project_one_title' => esc_html__('Cras tempus', 'webriti-companion'),
            'project_two_thumb' => QUALITY_TEMPLATE_DIR_URI . '/images/project_thumb1.png',
            'project_two_title' => esc_html__('Cras tempus', 'webriti-companion'),
            'project_three_thumb' => QUALITY_TEMPLATE_DIR_URI . '/images/project_thumb2.png',
            'project_three_title' => esc_html__('Cras tempus', 'webriti-companion'),
            'project_four_thumb' => QUALITY_TEMPLATE_DIR_URI . '/images/project_thumb3.png',
            'project_four_title' => esc_html__('Cras tempus', 'webriti-companion'),
            //BLOG
            'home_blog_enabled' => true,
            'blog_heading' => esc_html__('Proin Tincidunt', 'webriti-companion'),
            'home_blog_description' => __('Cras <b>Eros</b> Elit', 'webriti-companion'),
            'home_meta_section_settings' => false,
            //Custom css
            'webrit_custom_css' => '',
            //Footer Customization
            'footer_copyright_text' => '<p>' . __('<a href="https://wordpress.org">Proudly powered by WordPress</a> | Theme: <a href="https://webriti.com" rel="nofollow">Quality</a> by Webriti', 'webriti-companion') . '</p>',
        );
    }

}

# This function added in case if a user directly update -
# the plugin before update the Quality child theme

if (!function_exists('webriti_companion_quality_green_default_data')) {
    function webriti_companion_quality_green_default_data() {
        $quality_green_current_options = wp_parse_args(get_option('quality_pro_options', array()), webriti_companion_quality_theme_data_setup());
        if (get_option('quality_user', 'new')=='old' || $quality_green_current_options['text_title'] != '' || $quality_green_current_options['upload_image_logo'] != '' || $quality_green_current_options['webrit_custom_css'] == 'nomorenow') {
            $array_new = array(
                'header_center_layout_setting' => 'default',
                'service_box_layout_setting' => 'default',
            );
        } else {
            $array_new = array(
                'header_center_layout_setting' => 'center',
                'service_box_layout_setting' => 'box',
            );
        }
        $array_old = array(
            // general settings
            'footer_copyright_text' => '<p>' . __('<a href="https://wordpress.org">Proudly powered by WordPress</a> | Theme: <a href="https://webriti.com" rel="nofollow">Quality Green</a> by Webriti', 'webriti-companion') . '</p>',
        );
        return $result = array_merge($array_new, $array_old);
    }
}

if (!function_exists('webriti_companion_quality_orange_default_data')) {
    // Theme Default Data
    function webriti_companion_quality_orange_default_data() {
        $quality_orange_current_options = wp_parse_args(get_option('quality_pro_options', array()), webriti_companion_quality_theme_data_setup());
        if (get_option('quality_user', 'new')=='old' || $quality_orange_current_options['text_title'] != '' || $quality_orange_current_options['upload_image_logo'] != '' || $quality_orange_current_options['webrit_custom_css'] == 'nomorenow') {
            $array_new = array(
                'header_classic_layout_setting' => 'default',
                'service_rotate_layout_setting' => 'default',
            );
        } 
        else {
            $array_new = array(
                'header_classic_layout_setting' => 'classic',
                'service_rotate_layout_setting' => 'rotate',
            );
        }
        $array_old = array(
            // general settings
            'footer_copyright_text' => '<p>' . __('<a href="https://wordpress.org">Proudly powered by WordPress</a> | Theme: <a href="https://webriti.com" rel="nofollow">Quality Orange</a> by Webriti', 'webriti-companion') . '</p>',
        );
        return $result = array_merge($array_new, $array_old);
    }
}

if (!function_exists('webriti_companion_quality_blue_default_data')) {
    function webriti_companion_quality_blue_default_data() {
        $quality_blue_current_options = wp_parse_args(get_option('quality_pro_options', array()), webriti_companion_quality_theme_data_setup());
        if (get_option('quality_user', 'new')=='old' || $quality_blue_current_options['text_title'] != '' || $quality_blue_current_options['upload_image_logo'] != '' || $quality_blue_current_options['webrit_custom_css'] == 'nomorenow') {
            $array_new = array(
                'header_classic_layout_setting' => 'default',
                'service_blink_layout_setting' => 'default',
            );
        } 
        else {
            $array_new = array(
                'header_classic_layout_setting' => 'classic',
                'service_blink_layout_setting' => 'blink',
            );
        }
        $array_old = array(
            // general settings
            'footer_copyright_text' => '<p>' . __('<a href="https://wordpress.org">Proudly powered by WordPress</a> | Theme: <a href="https://webriti.com" rel="nofollow">Quality Blue</a> by Webriti', 'webriti-companion') . '</p>',
        );
        return $result = array_merge($array_new, $array_old);
    }
}

if (!function_exists('webriti_companion_heroic_default_data')) {
    function webriti_companion_heroic_default_data() {
        $heroic_current_options = wp_parse_args(get_option('quality_pro_options', array()), webriti_companion_quality_theme_data_setup());
        if (get_option('quality_user', 'new')=='old' || $heroic_current_options['text_title'] != '' || $heroic_current_options['upload_image_logo'] != '' || $heroic_current_options['webrit_custom_css'] == 'nomorenow') {
            $array_new = array(
                'header_sticky_layout_setting' => 'default',
                'service_slide_layout_setting' => 'default',
            );
        } 
        else {
            $array_new = array(
                'header_sticky_layout_setting' => 'sticky',
                'service_slide_layout_setting' => 'slide',
            );
        }
        $array_old = array(
            // general settings
            'footer_copyright_text' => '<p>' . __('<a href="https://wordpress.org">Proudly powered by WordPress</a> | Theme: <a href="https://webriti.com" rel="nofollow">Heroic</a> by Webriti', 'webriti-companion') . '</p>',
        );
        return $result = array_merge($array_new, $array_old);
    }
}