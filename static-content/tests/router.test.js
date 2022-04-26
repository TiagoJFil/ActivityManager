import router from "../app/router";
import handlers from "../app/handlers";


describe('Route Tests', () => {
    var assert = require('chai').assert;

    const routes = [
        'home',
        'sports',
        'sports/:sid',
        'users',
        'users/:uid',
        'routes',
        'routes/:rid',
        'sports/:sid/users',
        'sports/:sid/activities',
        'users/:uid/activities',
        'sports/:sid/activities/:aid',
    ]

    it('Route to #home should work',() => {


    })
    it('Route to #home should work',() => {

    })


})