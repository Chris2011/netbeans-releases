<?php

$test1 = [
    'TEST1' => [
        'path1' => 'test',
        'path2' => 'test',
        'path3' => [
            'path4' => 'test',
            'path5' => 'test',
            'path6' => 'test'
        ],
    ],
    'TEST2' => [
        'path1' => 'test',
        'path2' => 'test',
        'path3' => 'test',
    ],
    'TEST3' => [
        'path1' => 'test',
        'path2' => 'test',
        'path3' => 'test',
    ],
    'TEST4' => [
        'path1' => 'test',
        'path2' => 'test',
        'path3' => 'test',
    ],
];

$test2 = array(
    'TEST1' => array(
        'path1' => 'test',
        'path2' => 'test',
        'path3' => array(
            'path4' => 'test',
            'path5' => 'test',
        ),
    ),
    'TEST2' => array(
        'path1' => 'test',
        'path2' => 'test',
        'path3' => 'test',
    ),
    'TEST3' => array(
        'path1' => 'test',
        'path2' => 'test',
        'path3' => 'test',
    ),
    'TEST4' => array(
        'path1' => 'test',
        'path3' => 'test',
    ),
);

// Nested arrays are on the same line:
$test3 = ["test1" => ["test2" => ["test3" => "test4"]]];

$test4 = array("test1" => array(
        "test2" => array("test3" => "test4")
    )
);
