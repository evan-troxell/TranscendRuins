<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

/**
 * @package Starter Sites
 * @since 1.0
 */

/**
 * Set import files
 */
if ( !function_exists( 'webriti_companion_starter_sites_import_files' ) ) {

    function webriti_companion_starter_sites_import_files() {
        // Demos url
        $demo_url = 'https://webriticom.kinsta.cloud/startersites/';
        return array(
            array(
                'import_file_name'              =>  esc_html__('Wallstreet', 'webriti-companion'),
                'import_file_url'               =>  $demo_url . 'wallstreet/lite/sample-data.xml',
                'import_widget_file_url'        =>  $demo_url . 'wallstreet/lite/widgets.wie',
                'import_customizer_file_url'    =>  $demo_url . 'wallstreet/lite/customize-export.dat'
            )
        );
    }

}
add_filter( 'pt-ocdi/import_files', 'webriti_companion_starter_sites_import_files' );

/**
 * Define actions that happen after import
 */
if ( !function_exists( 'webriti_companion_starter_sites_after_import_mods' ) ) {

    function webriti_companion_starter_sites_after_import_mods() {

        //Assign the menu
        $main_menu = get_term_by( 'name', 'Primary Menu', 'nav_menu' );
        set_theme_mod( 'nav_menu_locations', array(
                'primary' => $main_menu->term_id,
            )
        );

        // Use WP_Query to get the 'Home' and 'Blog' pages
        $front_page = new WP_Query( array(
            'post_type'  => 'page',
            'title'      => 'Home',
            'post_status'=> 'publish',
            'posts_per_page' => 1
        ));

        $blog_page = new WP_Query( array(
            'post_type'  => 'page',
            'title'      => 'Blog',
            'post_status'=> 'publish',
            'posts_per_page' => 1
        ));

        if ($front_page->have_posts()) {
            $front_page_id = $front_page->posts[0]->ID;
        } else {
            $front_page_id = 0;
        }

        if ($blog_page->have_posts()) {
            $blog_page_id = $blog_page->posts[0]->ID;
        } else {
            $blog_page_id = 0;
        }

        // Assign the static front page and the blog page
        update_option( 'show_on_front', 'page' );
        update_option( 'page_on_front', $front_page_id );
        update_option( 'page_for_posts', $blog_page_id );

        // Reset post data to prevent conflicts
        wp_reset_postdata();
    }

}
add_action( 'pt-ocdi/after_import', 'webriti_companion_starter_sites_after_import_mods' );

// Custom CSS for OCDI plugin
function webriti_companion_wallstreet_starter_sites_ocdi_css() { ?>
    <style >
       .ocdi__intro-text, .ocdi__theme-about .ocdi__theme-about-info {
          display: none;
        }
        .ocdi__theme-about .ocdi__theme-about-screenshots img {
            width: 50%;
        }
    </style>
    <?php
}
add_action('admin_enqueue_scripts', 'webriti_companion_wallstreet_starter_sites_ocdi_css');

// Change the "One Click Demo Import" name from "Starter Sites" in Appearance menu
function webriti_companion_starter_sites_ocdi_plugin_page_setup( $default_settings ) {
    $default_settings['parent_slug'] = 'admin.php';
    $default_settings['page_title']  = esc_html__( 'One Click Demo Import' , 'webriti-companion' );
    $default_settings['menu_title']  = esc_html__( 'Starter Sites' , 'webriti-companion' );
    $default_settings['capability']  = 'import';
    $default_settings['menu_slug']   = 'one-click-demo-import';
    return $default_settings;
}
add_filter( 'ocdi/plugin_page_setup', 'webriti_companion_starter_sites_ocdi_plugin_page_setup' );

// Register required plugins for the demo's
function webriti_companion_starter_sites_register_plugins( $plugins ) {
    // List of plugins used by all theme demos.
    $theme_plugins = [
        [ 
            'name'     =>   'Contact Form 7',
            'slug'     =>   'contact-form-7',
            'required' =>   true,
        ],

        [   
            'name'     =>   'Elementor', 
            'slug'     =>   'elementor',
            'required' =>   true,
        ],
        
        [
            'name'     =>   'ElementsKit Lite',
            'slug'     =>   'elementskit-lite',
            'required' =>   true,
        ],
        [ 
            'name'     =>   'WooCommerce',
            'slug'     =>   'woocommerce',
            'required' =>   true,
        ]
    ];
    return array_merge( $plugins, $theme_plugins );
}
add_filter( 'ocdi/register_plugins', 'webriti_companion_starter_sites_register_plugins' );

/**
* Remove branding
*/
add_filter( 'pt-ocdi/disable_pt_branding', '__return_true' );