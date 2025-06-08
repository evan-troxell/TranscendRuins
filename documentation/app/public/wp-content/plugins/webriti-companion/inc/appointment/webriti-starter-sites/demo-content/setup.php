<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

/*
Functions to provide support for the One Click Demo Import plugin (wordpress.org/plugins/one-click-demo-import)

Set import files */
if ( !function_exists( 'webriti_companion_starter_sites_import_files' ) ) {
    function webriti_companion_starter_sites_import_files() {

        $demo_url = "https://webriti.com/startersites/appointment/";
        $theme = wp_get_theme();

        if ('Appointment' == $theme->name) {
            $local_import               = $demo_url . 'lite/default/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/default/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/default/customize-export.dat';
            $preview_url                = 'https://appointment.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointment.jpg';
        }
        if ('Appointment Green' == $theme->name) {
            $local_import               = $demo_url . 'lite/appointment-green/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/appointment-green/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/appointment-green/customize-export.dat';
            $preview_url                = 'https://appointment-green.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointment-green.jpg';
        }
        if ('Appointment Blue' == $theme->name) {
            $local_import               = $demo_url . 'lite/appointment-blue/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/appointment-blue/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/appointment-blue/customize-export.dat';
            $preview_url                = 'https://appointment-blue.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointment-blue.jpg';
        }
        if ('Appointment Red' == $theme->name) {
            $local_import               = $demo_url . 'lite/appointment-red/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/appointment-red/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/appointment-red/customize-export.dat';
            $preview_url                = 'https://appointment-red.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointment-red.jpg';
        }
        if ('Appointee' == $theme->name) {
            $local_import               = $demo_url . 'lite/appointee/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/appointee/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/appointee/customize-export.dat';
            $preview_url                = 'https://appointee.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointee.jpg';
        }
        if ('Appointment Dark' == $theme->name) {
            $local_import               = $demo_url . 'lite/appointment-dark/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/appointment-dark/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/appointment-dark/customize-export.dat';
            $preview_url                = 'https://appointment-dark.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/appointment-dark.jpg';
        }
        if ('vice' == $theme->name) {
            $local_import               = $demo_url . 'lite/vice/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/vice/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/vice/customize-export.dat';
            $preview_url                = 'https://vice.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/vice.jpg';
        }
        if ('Shk Corporate' == $theme->name) {
            $local_import               = $demo_url . 'lite/shk-corporate/sample-data.xml';
            $local_import_widget        = $demo_url . 'lite/shk-corporate/widgets.wie';
            $local_import_customizer    = $demo_url . 'lite/shk-corporate/customize-export.dat';
            $preview_url                = 'https://shk-corporate.webriti.com/';
            $preview_image_url          = $demo_url . 'thumbnail/shk-corporate.jpg';
        }
        return array(
            //Default Demo
            array(
                'import_file_name'              => __('Default', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'import_file_url'               => $local_import,
                'import_widget_file_url'        => $local_import_widget,
                'import_customizer_file_url'    => $local_import_customizer,
                'preview_url'                   => $preview_url,
                'import_preview_image_url'      => $preview_image_url,
            ),

            //Elementor Demo's
            array(
                'import_file_name'              => __('Business', 'webriti-companion'),
                'categories'                    => ['Elementor'],
               'import_file_url'                => $demo_url . 'lite/business/sample-data.xml',
                'import_widget_file_url'        => $demo_url . 'lite/business/widgets.wie',
                'import_customizer_file_url'    => $demo_url . 'lite/business/customize-export.dat',
                'preview_url'                   => 'https://ap-business.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/business.jpg',
            ),

            array(
                'import_file_name'              => __('Restaurants', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'import_file_url'               => $demo_url . 'lite/restaurants/sample-data.xml',
                'import_widget_file_url'        => $demo_url . 'lite/restaurants/widgets.wie',
                'import_customizer_file_url'    => $demo_url . 'lite/restaurants/customize-export.dat',
                'preview_url'                   => 'https://ap-restaurants.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/restaurants.jpg',
            ),
            
            array(
                'import_file_name'              => __('Corporate', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'preview_url'                   => 'https://ap-corporate.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/corporate.jpg',
            ),
          
            array(
                'import_file_name'              => __('Maintenance', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'preview_url'                   => 'https://ap-maintenance.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/maintenance.jpg',
            ),
          
            array(
                'import_file_name'              => __('Education', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'preview_url'                   => 'https://ap-education.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/education.jpg',
            ),
          
            array(
                'import_file_name'              => __('Architect', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'preview_url'                   => 'https://ap-architect.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/architect.jpg',
            ),
            
            array(
                'import_file_name'              => __('Finance', 'webriti-companion'),
                'categories'                    => ['Elementor'],
                'preview_url'                   => 'https://ap-finance.webriti.com/',
                'import_preview_image_url'      => $demo_url . 'thumbnail/finance.jpg',
            ),

            //Gutenberg Demo's
            array(
                'import_file_name'              => __('Appointment-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'import_file_url'               => $demo_url . 'lite/gutenberg/appointment/sample-data.xml',
                'import_widget_file_url'        => $demo_url . 'lite/gutenberg/appointment/widgets.wie',
                'import_customizer_file_url'    => $demo_url . 'lite/gutenberg/appointment/customize-export.dat',
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-one',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/appointment.jpg',
            ),


            array(
                'import_file_name'              => __('Growkit-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'import_file_url'               => $demo_url . 'lite/gutenberg/growkit/sample-data.xml',
                'import_widget_file_url'        => $demo_url . 'lite/gutenberg/growkit/widgets.wie',
                'import_customizer_file_url'    => $demo_url . 'lite/gutenberg/growkit/customize-export.dat',
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-two',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/growkit.jpg',
            ),

            array(
                'import_file_name'              => __('Building-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'import_file_url'               => $demo_url . 'lite/gutenberg/building/sample-data.xml',
                'import_widget_file_url'        => $demo_url . 'lite/gutenberg/building/widgets.wie',
                'import_customizer_file_url'    => $demo_url . 'lite/gutenberg/building/customize-export.dat',
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-three',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/building.jpg',
            ),

            array(
                'import_file_name'              => __('Appointment-Pro-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-pro-one',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/appointment-pro.jpg',
            ),

            array(
                'import_file_name'              => __('Business-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-pro-two',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/business.jpg',
            ),

            array(
                'import_file_name'              => __('Corporate-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-pro-three',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/corporate.jpg',
            ),

            array(
                'import_file_name'              => __('Digital-Agency-Gutenberg', 'webriti-companion'),
                'categories'                    => ['Gutenberg'],
                'preview_url'                   => 'https://demo-appointment.webriti.com/demo-pro-four',
                'import_preview_image_url'      => $demo_url . 'thumbnail/gutenberg/digital-agency.jpg',
            ),
        );
    }
}
add_filter( 'pt-ocdi/import_files', 'webriti_companion_starter_sites_import_files' );

/* Define actions that happen after import */
if ( !function_exists( 'webriti_companion_starter_sites_after_import_mods' ) ) {
    function webriti_companion_starter_sites_after_import_mods($selected_import) {

        // List of plugins used by all theme demos.
        if ( 'Gutenberg' === $selected_import['categories'][0]) {
            $main_menu = get_term_by( 'name', 'Primary Menu', 'nav_menu' );
        }else{
            $main_menu = get_term_by( 'name', 'Menu 1', 'nav_menu' );
        }   

        set_theme_mod( 'nav_menu_locations', array(
                'primary' => $main_menu->term_id,
            )
        );
     
        // Assign the static front page and the blog page
        $front_page_query = new WP_Query( array(
            'post_type'      => 'page',
            'post_status'    => 'publish',
            'posts_per_page' => 1,
            'title'          => 'Home',
        ) );

        $front_page = $front_page_query->posts[0];
        wp_reset_postdata();

        $blog_page_query = new WP_Query( array(
            'post_type'      => 'page',
            'post_status'    => 'publish',
            'posts_per_page' => 1,
            'title'          => 'Blog',
        ) );

        $blog_page = $blog_page_query->posts[0];
        wp_reset_postdata();

        update_option( 'show_on_front', 'page' );
        update_option( 'page_on_front', $front_page->ID );
        update_option( 'page_for_posts', $blog_page->ID );

        // Update all posts to trigger post update actions
        $args = array(
            'post_type' => 'post',
            'posts_per_page' => -1,
        );

        $appoint_posts = get_posts($args);
        foreach ($appoint_posts as $appoint_post){
            $appoint_post->post_title = $appoint_post->post_title.'';
            wp_update_post( $appoint_post );
        }

        $appointment_setting = wp_parse_args(get_option('appointment_options', array()), appointment_theme_setup_data());
        $cat1 = get_cat_ID('Adventure');
        $cat2 = get_cat_ID('Latest News');
        $cat_id = array($cat1, $cat2);
        $appointment_setting['blog_selected_category_id']= $cat_id;
        update_option('appointment_options', $appointment_setting );

        $theme = wp_get_theme();
        if ('Appointment Green' == $theme->name) {
            $appointment_green_setting = wp_parse_args(get_option('appointment_options', array()), appointment_theme_setup_data());
            $cat1 = get_cat_ID('Business');
            $cat2 = get_cat_ID('Corporate');
            $cat_id = array($cat1, $cat2);
            $appointment_green_setting['slider_select_category']= $cat_id;
            update_option('appointment_options', $appointment_green_setting );
        }

    }
}
add_action( 'pt-ocdi/after_import', 'webriti_companion_starter_sites_after_import_mods' );

// Custom CSS for OCDI plugin
function webriti_companion_ocdi_css() { ?>
    <style >
        .ocdi__gl-item:is([data-name="corporate"],[data-name="maintenance"],[data-name="education"],[data-name="architect"],[data-name="finance"],[data-name="appointment-pro-gutenberg"],[data-name="business-gutenberg"],[data-name="corporate-gutenberg"],[data-name="digital-agency-gutenberg"]) .ocdi__gl-item-buttons .button-primary, .ocdi .ocdi__theme-about {
            display: none;
        }
    </style>
<?php }
add_action('admin_enqueue_scripts', 'webriti_companion_ocdi_css');

// Change the "One Click Demo Import" name from "Starter Sites" in Appearance menu
function webriti_companion_ocdi_plugin_page_setup( $default_settings ) {
    $default_settings['parent_slug'] = 'themes.php';
    $default_settings['page_title']  = esc_html__( 'One Click Demo Import' , 'webriti-companion' );
    $default_settings['menu_title']  = esc_html__( 'Starter Sites' , 'webriti-companion' );
    $default_settings['capability']  = 'import';
    $default_settings['menu_slug']   = 'one-click-demo-import';

    return $default_settings;
}
add_filter( 'ocdi/plugin_page_setup', 'webriti_companion_ocdi_plugin_page_setup' );

// Register required plugins for the demo's
function webriti_companion_starter_sites_register_plugins( $plugins ) {
    $theme_plugins = [];
    if(isset($_GET['import'])){
        $import_id=absint($_GET['import']);
        $import_array=webriti_companion_starter_sites_import_files();
        $impot_file_cat=$import_array[$import_id]['categories'];
        // List of plugins used by all theme demos.
        if ( 'Elementor' === $impot_file_cat[0] ) {
          $theme_plugins = [
              [ // A WordPress.org plugin repository example.
                'name'     => __('Elementor', 'webriti-companion'), // Name of the plugin.
                'slug'     => 'elementor', // Plugin slug - the same as on WordPress.org plugin repository.
                'required' => true,                     // If the plugin is required or not.
              ],
              [ // A WordPress.org plugin repository example.
                'name'     => __('Contact Form 7', 'webriti-companion'), // Name of the plugin.
                'slug'     => 'contact-form-7', // Plugin slug - the same as on WordPress.org plugin repository.
                'required' => true,                     // If the plugin is required or not.
              ],
          ];
        }
        if( 'Gutenberg' === $impot_file_cat[0] ) {
            $theme_plugins = [
                  [ // A WordPress.org plugin repository example.
                    'name'     => __('Spice Blocks', 'webriti-companion'), // Name of the plugin.
                    'slug'     => 'spice-blocks', // Plugin slug - the same as on WordPress.org plugin repository.
                    'required' => true,                     // If the plugin is required or not.
                  ],
                  [ // A WordPress.org plugin repository example.
                    'name'     => __('Contact Form 7', 'webriti-companion'), // Name of the plugin.
                    'slug'     => 'contact-form-7', // Plugin slug - the same as on WordPress.org plugin repository.
                    'required' => true,                     // If the plugin is required or not.
                  ],
                  [ // A WordPress.org plugin repository example.
                    'name'     => __('Yoast SEO ', 'webriti-companion'), // Name of the plugin.
                    'slug'     => 'wordpress-seo', // Plugin slug - the same as on WordPress.org plugin repository.
                    'required' => true,                     // If the plugin is required or not.
                  ],
              ];
        }
    }
    return array_merge( $plugins, $theme_plugins );
}
add_filter( 'ocdi/register_plugins', 'webriti_companion_starter_sites_register_plugins' );


/* Remove branding */
add_filter( 'pt-ocdi/disable_pt_branding', '__return_true' );

// Custom CSS for OCDI plugin
function webriti_companion_ele_css() { ?>
    <style >
        .service-section .elementor-inner-column:hover .elementor-widget-container .elementor-icon-wrapper .elementor-icon svg,
        .service-column .elementor-inner-column:hover .elementor-widget-container .elementor-icon-wrapper .elementor-icon svg {
            fill: #FFFFFF ;
        }
    </style>
<?php }
add_action('wp_head', 'webriti_companion_ele_css');

//Repace unicode
function webriti_companion_decode_unicode_entities($content) {
    // Decode common Unicode entities back to their HTML equivalents
    $content = str_replace('u003c', '<', $content);
    $content = str_replace('u003e', '>', $content);
    $content = str_replace('u0026', '&', $content);
    $content = str_replace('u0022', '"', $content);
    $content = str_replace('u0027', "'", $content);

    // Decode any other Unicode sequences
    $content = preg_replace_callback('/u([0-9a-fA-F]{4})/', function($matches) {
        return chr(hexdec($matches[1]));
    }, $content);

    return $content;
}

function webriti_companion_after_import_process_content() {
    // Get all posts
    $posts = get_posts(array(
        'numberposts' => -1,
        'post_type' => 'any',
        'post_status' => 'any',
    ));

    // Loop through each post and decode Unicode entities
    foreach ($posts as $post) {
        $content = $post->post_content;
        $decoded_content = webriti_companion_decode_unicode_entities($content);

        // If content was changed, update the post
        if ($content !== $decoded_content) {
            wp_update_post(array(
                'ID' => $post->ID,
                'post_content' => $decoded_content
            ));
        }
    }
}
add_action('pt-ocdi/after_import', 'webriti_companion_after_import_process_content');