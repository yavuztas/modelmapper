package dev.yavuztas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import dev.yavuztas.domain.Game;
import dev.yavuztas.domain.Player;
import dev.yavuztas.dto.GameDTO;
import java.time.Instant;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

public class ModelMapperTest {

  ModelMapper mapper;

  @BeforeEach
  public void setup(){
    this.mapper = new ModelMapper();
  }

  @Test
  public void whenMapGameWithDeepMapping_convertsToDTO_CollectionProxyNotWorking(){
    final Game game = new Game(1L, "Game 1");
    game.setCreator(new Player(1L, "John"));
    game.addPlayer(new Player(2L, "Bob"));

    final TypeMap<Game, GameDTO> propertyMap = this.mapper.createTypeMap(Game.class, GameDTO.class);
    propertyMap.addMappings(mapper -> mapper.map(src -> src.getPlayers().size(), GameDTO::setTotalPlayers));

    final GameDTO gameDTO = this.mapper.map(game, GameDTO.class);

    assertEquals(game.getId(), gameDTO.getId());
    assertEquals(game.getName(), gameDTO.getName());
    assertEquals(game.getPlayers().size(), gameDTO.getTotalPlayers());
  }

  @Test
  public void whenMapGameWithCustomConverter_convertsToDTO(){
    final Game game = new Game(1L, "Game 1");
    game.setCreator(new Player(1L, "John"));
    game.addPlayer(new Player(2L, "Bob"));

    final TypeMap<Game, GameDTO> propertyMap = this.mapper.createTypeMap(Game.class, GameDTO.class);
    final Converter<Collection, Integer> collectionToSize = c -> c.getSource().size();
    propertyMap.addMappings(mapper -> {
      mapper.using(collectionToSize).map(Game::getPlayers, GameDTO::setTotalPlayers);
    });

    final GameDTO gameDTO = this.mapper.map(game, GameDTO.class);

    assertEquals(game.getId(), gameDTO.getId());
    assertEquals(game.getName(), gameDTO.getName());
    assertEquals(game.getPlayers().size(), gameDTO.getTotalPlayers());
  }

}
