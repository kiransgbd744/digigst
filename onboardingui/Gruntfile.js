module.exports = function (grunt) {
    "use strict";
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        clean: {
            build: ['dist']
        },

        copy: {
            build: {
                files: [{
                    expand: true,
                    cwd: 'webapp',
                    src: ['**/*.*', '!**/*.js'],
                    dest: 'dist/'
                }, {
                    src: 'package.json',
                    dest: 'dist/'
                }]
            },
            debug: {
                files: [{
                    expand: true,
                    cwd: 'webapp/',
                    src: ['**/*.js', '!**/*-dbg*.js'],
                    dest: 'dist/',
                    rename: function (dest, src) {
                        var path = require('path');
                        var dirname = path.dirname(src);
                        var basename = path.basename(src, '.js');
                        if (basename.match(/\.controller$/)) {
                            var newBasename = basename.replace(/\.controller$/, '-dbg.controller');
                        } else {
                            var newBasename = basename + '-dbg';
                        }
                        return path.join(dest, dirname, newBasename + '.js');
                    }
                }]
            },
            xsapp: {
                src: 'xs-app.json',
                dest: 'dist/',
                options: {
                    process: function (content) {
                        var jsonContent = JSON.parse(content);
                        jsonContent.routes.forEach(function (route) {
                            if (route.localDir) {
                                route.localDir = './';
                            }
                        });
                        return JSON.stringify(jsonContent, null, 2);
                    }
                }
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            build: {
                files: [{
                    expand: true,
                    cwd: 'webapp/',
                    src: ['**/*.js', '!**/*-dbg*.js'],
                    dest: 'dist/',
                    rename: function (dest, src) {
                        var path = require('path');
                        var dirname = path.dirname(src);
                        var basename = path.basename(src, '.js');
                        return path.join(dest, dirname, basename + '.js');
                    }
                }]
            }
        },

        openui5_preload: {
            component: {
                options: {
                    resources: {
                        cwd: 'dist',
                        prefix: 'com/ey/onboarding',
                        src: [
                            '**/*.js',
                            '**/*.xml',
                            '**/*.properties',
                            '**/manifest.json',
                            '!**/*-dbg*.js'
                        ]
                    },
                    dest: 'dist',
                    compress: true
                },
                components: true
            }
        }
    });

    // Load grunt plugins
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-openui5');

    grunt.registerTask('default', [
        'clean',
        'copy',
        'uglify',
        'openui5_preload'
    ]);
};