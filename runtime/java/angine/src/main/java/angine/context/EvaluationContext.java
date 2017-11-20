package angine.context;


import angine.util.IIdentifiable;

public class EvaluationContext{
    public IIdentifiable subject;
    public IIdentifiable entity;
    public Object action;

    public EvaluationContext(IIdentifiable subject, IIdentifiable entity, Object action){
        this.subject = subject;
        this.entity = entity;
        this.action = action;
    }
}
