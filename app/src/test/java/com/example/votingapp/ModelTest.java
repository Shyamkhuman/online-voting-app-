package com.example.votingapp;

import com.example.votingapp.models.Candidate;
import com.example.votingapp.models.User;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for model classes.
 */
public class ModelTest {

    @Test
    public void user_defaultHasVotedIsFalse() {
        User user = new User("uid1", "Alice", "alice@example.com");
        assertFalse(user.isHasVoted());
        assertEquals("", user.getVotedFor());
    }

    @Test
    public void user_settersWork() {
        User user = new User("uid1", "Alice", "alice@example.com");
        user.setHasVoted(true);
        user.setVotedFor("candidate_1");
        assertTrue(user.isHasVoted());
        assertEquals("candidate_1", user.getVotedFor());
    }

    @Test
    public void candidate_defaultVoteCountIsZero() {
        Candidate c = new Candidate("c1", "Bob", "http://example.com/bob.jpg", "A great candidate");
        assertEquals(0, c.getVoteCount());
        assertEquals("Bob", c.getName());
        assertEquals("c1", c.getCandidateId());
    }

    @Test
    public void candidate_settersWork() {
        Candidate c = new Candidate("c1", "Bob", "http://example.com/bob.jpg", "Manifesto");
        c.setVoteCount(42);
        assertEquals(42, c.getVoteCount());
        c.setName("Robert");
        assertEquals("Robert", c.getName());
    }

    @Test
    public void candidate_noArgConstructor() {
        Candidate c = new Candidate();
        assertNull(c.getCandidateId());
        assertEquals(0, c.getVoteCount());
    }

    @Test
    public void user_noArgConstructor() {
        User u = new User();
        assertNull(u.getUid());
        assertFalse(u.isHasVoted());
    }
}
