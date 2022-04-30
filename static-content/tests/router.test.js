import router from "../app/router.js";

const assert = chai.assert;
const expect = chai.expect
describe('Router Tests', () => {
    
    const baseHandler = (number) => {
        expect(number).to.equal(1) 
    }
    const notFoundHandler = () => {
        expect(1).to.equal(2) // Fails if called
    }
    
    //fails if called
    router.addDefaultNotFoundRouteHandler(notFoundHandler)

   const routesToAssert = [
    'home', 
    'sports', 
    'sports/:sid', 
    'users', 
    'users/:uid', 
    'routes', 
    'routes/:rid', 
    'activities', 
    'sports/:sid/activities/:aid',
    'sports/:sid/users', 
    'sports/:sid/activities', 
    'users/:uid/activities', 
   ]

   const expectedHandlers = [
       
   ]
    
   /******* Nao façam routes com cenas da app, depois nao da para adicionar, porque a route é só uma  metam umas quaisquer depois testas a app com as cenas certas */
    it('Simple route without placeholders',() => {

        router.addRouteHandler('z', baseHandler)
        // When
        const res = router.getRouteHandler('z')
       
        // Then
        
        assert.deepEqual(res, {handler: baseHandler, params: {}, query: {} })
        res.handler(1)
    })

    it('Route to sports with a value on a param should work',() => {
        router.addRouteHandler('xyz/:a',baseHandler)
        const res = router.getRouteHandler('xyz/43')
        
        assert.deepEqual(res,{handler: baseHandler, params: {a: '43'} , query: {}})
        res.handler(1)
    })

    it('Route with two values on params should work', () => {
        router.addRouteHandler('x/:a/y/:b', baseHandler)

        const routeHandler = router.getRouteHandler('x/1/y/2')

        assert.deepEqual(routeHandler, {handler: baseHandler, params: {a: '1', b: '2'}, query: {}})
        routeHandler.handler(1)
    })
    
    it('Route with a value on param and other on query should work', () => {
        router.addRouteHandler('z/:a/y', baseHandler)

        const routeHandler = router.getRouteHandler('z/abcdef/y?b=2')

        assert.deepEqual(routeHandler, {handler: baseHandler, params: {a: 'abcdef'}, query: {b: '2'}})

    })
    
    it('Invalid route gives not found', () => {
      
        const routeHandler = router.getRouteHandler('z/z/z/z/z')

        assert.deepEqual(routeHandler, {handler: notFoundHandler, params: {} , query: {}})  
    })
    
    
})