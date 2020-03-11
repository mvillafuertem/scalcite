if (process.env.NODE_ENV === "production") {
    const opt = require("./scalcite-example-frontend-opt.js");
    opt.main();
    module.exports = opt;
} else {
    var exports = window;
    exports.require = require("./scalcite-example-frontend-fastopt-entrypoint.js").require;
    window.global = window;

    const fastOpt = require("./scalcite-example-frontend-fastopt.js");
    fastOpt.main()
    module.exports = fastOpt;

    if (module.hot) {
        module.hot.accept();
    }
}
