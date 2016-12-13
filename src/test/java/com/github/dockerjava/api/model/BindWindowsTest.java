package com.github.dockerjava.api.model;

import static com.github.dockerjava.api.model.AccessMode.ro;
import static com.github.dockerjava.api.model.AccessMode.rw;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import org.testng.annotations.Test;

public class BindWindowsTest {

    @Test
    public void parseUsingDefaultAccessMode() {
        Bind bind = Bind.parse("c:\\host:c:\\container");
        assertThat(bind.getPath(), is("c:\\host"));
        assertThat(bind.getVolume().getPath(), is("c:\\container"));
        assertThat(bind.getAccessMode(), is(AccessMode.DEFAULT));
        assertThat(bind.getSecMode(), is(SELContext.none));
        assertThat(bind.getNoCopy(), nullValue());
        assertThat(bind.getPropagationMode(), is(PropagationMode.DEFAULT_MODE));
    }

    @Test
    public void parseReadWrite() {
        Bind bind = Bind.parse("c:\\host:c:\\container:rw");
        assertThat(bind.getPath(), is("c:\\host"));
        assertThat(bind.getVolume().getPath(), is("c:\\container"));
        assertThat(bind.getAccessMode(), is(rw));
        assertThat(bind.getSecMode(), is(SELContext.none));
        assertThat(bind.getNoCopy(), nullValue());
        assertThat(bind.getPropagationMode(), is(PropagationMode.DEFAULT_MODE));
    }

    @Test
    public void parseReadOnly() {
        Bind bind = Bind.parse("c:\\host:c:\\container:ro");
        assertThat(bind.getPath(), is("c:\\host"));
        assertThat(bind.getVolume().getPath(), is("c:\\container"));
        assertThat(bind.getAccessMode(), is(ro));
        assertThat(bind.getSecMode(), is(SELContext.none));
        assertThat(bind.getNoCopy(), nullValue());
        assertThat(bind.getPropagationMode(), is(PropagationMode.DEFAULT_MODE));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Error parsing Bind.*")
    public void parseInvalidAccessMode() {
        Bind.parse("c:\\host:c:\\container:xx");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Error parsing Bind 'nonsense'")
    public void parseInvalidInput() {
        Bind.parse("nonsense");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Error parsing Bind 'null'")
    public void parseNull() {
        Bind.parse(null);
    }

    @Test
    public void toStringReadOnly() {
        assertThat(Bind.parse("c:\\host:c:\\container:ro").toString(), is("c:\\host:c:\\container:ro"));
    }

    @Test
    public void toStringReadWrite() {
        assertThat(Bind.parse("c:\\host:c:\\container:rw").toString(), is("c:\\host:c:\\container:rw"));
    }

    @Test
    public void toStringDefaultAccessMode() {
        assertThat(Bind.parse("c:\\host:c:\\container").toString(), is("c:\\host:c:\\container:rw"));
    }
}
