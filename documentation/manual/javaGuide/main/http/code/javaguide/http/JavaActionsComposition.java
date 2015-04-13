package javaguide.http;

import play.Logger;
import play.cache.Cached;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
public class JavaActionsComposition extends Controller {

    // #verbose-action
    public class VerboseAction extends play.mvc.Action.Simple {
        public F.Promise<Result> call(Http.Context ctx) throws Throwable {
            Logger.info("Calling action for " + ctx);
            return delegate.call(ctx);
        }
    }
    // #verbose-action

    // #verbose-index
    @With(VerboseAction.class)
    public static Result verboseIndex() {
        return ok("It works!");
    }
    // #verbose-index

    // #authenticated-cached-index
    @Security.Authenticated
    @Cached(key = "index.result")
    public static Result authenticatedCachedIndex() {
        return ok("It works!");
    }
    // #authenticated-cached-index

    // #verbose-annotation
    @With(VerboseAnnotationAction.class)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface VerboseAnnotation {
        boolean value() default true;
    }
    // #verbose-annotation

    // #verbose-annotation-index
    @VerboseAnnotation(false)
    public static Result verboseAnnotationIndex() {
        return ok("It works!");
    }
    // #verbose-annotation-index

    // #verbose-annotation-action
    public class VerboseAnnotationAction extends Action<VerboseAnnotation> {
        public F.Promise<Result> call(Http.Context ctx) throws Throwable {
            if (configuration.value()) {
                Logger.info("Calling action for " + ctx);
            }
            return delegate.call(ctx);
        }
    }
    // #verbose-annotation-action

    static class User {
        public static Integer findById(Integer id) { return id; }
    }

    // #pass-arg-action
    public class PassArgAction extends play.mvc.Action.Simple {
        public F.Promise<Result> call(Http.Context ctx) throws Throwable {
            ctx.args.put("user", User.findById(1234));
            return delegate.call(ctx);
        }
    }
    // #pass-arg-action

    // #pass-arg-action-index
    @With(PassArgAction.class)
    public static Result passArgIndex() {
        Object user = ctx().args.get("user");
        return ok(Json.toJson(user));
    }
    // #pass-arg-action-index

}
